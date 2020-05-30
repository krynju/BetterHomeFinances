package com.example.betterhomefinances


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.databinding.FragmentCreateTransactionBinding
import com.example.betterhomefinances.handlers.*


interface OnUserListFragmentInteractionListener {


    fun onUserListFragmentInteraction(v: View, item: UserItem)
}

class CreateTransaction : Fragment(), OnUserListFragmentInteractionListener {
    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!
    var groupReferencePath: String? = null
    var transactionReferencePath: String? = null
    private var mode: String = "create"
    private lateinit var userAdapter: MyUsersRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as GroupReference?
        transactionReferencePath =
            arguments?.get("transactionReferencePath") as TransactionReference?
        if (transactionReferencePath != null) {
            mode = "edit"
        }
        userAdapter = MyUsersRecyclerViewAdapter(groupReferencePath!!)
    }

    fun lockAll() {

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        binding.addButton.setOnClickListener { createTransaction(it) }
        binding.transactionValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                print("afte")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                print("before")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ss = s.toString()
                val dd = ss.toDoubleOrNull()

                if (dd != null) {
                    userAdapter.distribute(dd)
                } else {
                    userAdapter.distribute(0.0)
                }
            }
        })
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = userAdapter
        return binding.root
    }

    fun createTransaction(v: View) {

        val tran_value = userAdapter.value
        if (tran_value == 0.0) return
        if (!userAdapter.indivitualValues.fold(true) { acc: Boolean, d: Double ->
                if (d < 0.0) {
                    acc.and(false); acc
                } else {
                    acc
                }
            }) {
            return
        }
//        userAdapter.lockAll()
        val a =
            userAdapter.indivitualValues.mapIndexed { index, d -> userAdapter.data[index].reference!! to d }
                .toMap()
        val b = HashMap(a)
        TransactionHandler.createTransaction(
            groupReference = groupReferencePath!!,
            borrowers = b,
            title = binding.transactionTitle.text.toString(),
            category = "",
            description = binding.transactionDescription.text.toString(),
            lender = UserHandler.currentUserReference,
            value = tran_value
        ) {
            findNavController().navigateUp()
        }


    }

    override fun onUserListFragmentInteraction(v: View, item: UserItem) {
        TODO("Not yet implemented")
    }
}
