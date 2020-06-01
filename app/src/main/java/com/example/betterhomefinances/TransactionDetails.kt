package com.example.betterhomefinances

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.BorrowersListRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentTransactionDetailsBinding
import com.example.betterhomefinances.handlers.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import java.io.File
import kotlin.math.round


class TransactionDetails : Fragment() {

    private var _binding: FragmentTransactionDetailsBinding? = null
    private val binding get() = _binding!!

    private var groupReferencePath: GroupReference? = null
    private var transactionReferencePath: TransactionReference? = null
    private lateinit var transaction: Transaction
    private var adapter: BorrowersListRecyclerViewAdapter? = null
    var basePath: String? = null

    private lateinit var userNames: MutableMap<UserReference, UserDetails>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        basePath = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/"
        groupReferencePath = arguments?.get("groupReferencePath") as GroupReference?
        transactionReferencePath =
            arguments?.get("transactionReferencePath") as TransactionReference?

        transaction =
            TransactionHandler.getInstance(groupReferencePath!!).data.find { transactionItem -> transactionItem.reference == transactionReferencePath }?.transaction!!
        val groupItem: GroupItem = GroupHandler.data.find { it.reference == groupReferencePath }!!


        val userIds = groupItem.group.members.map { it.split("/")[1] }
        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get().addOnSuccessListener { querySnapshot: QuerySnapshot? ->
                userNames = querySnapshot!!.map { it.reference.path to it.toObject<UserDetails>() }
                    .toMap() as MutableMap<UserReference, UserDetails>
                if (this::transaction.isInitialized) {
                    updateNameInfo()
                    updateTransactionInfo()
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionDetailsBinding.inflate(inflater, container, false)
        updateTransactionInfo()
        FirestoreHandler.db.document(transactionReferencePath.toString())
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                assert(documentSnapshot!!.exists())
                transaction = documentSnapshot.toObject<Transaction>()!!
                if (this::userNames.isInitialized) {
                    updateNameInfo()
                    updateTransactionInfo()
                }
            }
        binding.recyclerView2.layoutManager = LinearLayoutManager(context)
        adapter =
            BorrowersListRecyclerViewAdapter()
        binding.recyclerView2.adapter = adapter
        binding.extendedFloatingActionButton.setOnClickListener { onEditClick(it) }
        return binding.root
    }

    fun updateTransactionInfo() {

        binding.transactionTitle.text = transaction.title
        binding.transactionDescription.text = transaction.description
        binding.valueField.text = (round(transaction.value * 100) / 100).toString()

        if (transaction.imageReference != "") {
            val currentPhotoPath =
                basePath + transaction.imageReference.split("/").last()

            if (!File(currentPhotoPath).exists()) {
                val ref = StorageHandler.mStorageRef!!.child(transaction.imageReference)
                ref.getFile(File(currentPhotoPath)).addOnSuccessListener {
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    binding.image.setImageBitmap(bitmap)
                }
            } else {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.image.setImageBitmap(bitmap)
                }
            }

    }

    fun updateNameInfo() {
        binding.whoPaid.text = userNames[transaction.lender]!!.name
        adapter!!.userNames = userNames
        adapter!!.data = transaction.borrowers.map {
            Pair<UserReference, Double>(
                it.key,
                it.value
            )
        } as ArrayList<Pair<UserReference, Double>>
        adapter!!.notifyDataSetChanged()
    }

    fun onEditClick(v: View) {
        val action = TransactionDetailsDirections.actionNavTransactionDetailsToNavCreateTransaction(
            groupReferencePath.toString(),
            transactionReferencePath,
            0.0F,
            null
        )
        findNavController().navigate(action)
    }


}
