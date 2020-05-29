package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as GroupReference?
        transactionReferencePath =
            arguments?.get("transactionReferencePath") as TransactionReference?
        if (transactionReferencePath != null) {
            mode = "edit"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        binding.addButton.setOnClickListener { createTransaction(it) }

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = MyUsersRecyclerViewAdapter(groupReferencePath!!)

        return binding.root
    }

    fun createTransaction(v: View) {
        val tran_value = binding.transactionValue.text.toString().toDoubleOrNull() ?: return

        val b = hashMapOf(
            UserHandler.currentUserDocumentReference.path to tran_value / 3.0,
            "users/uNMjYrRUfhDiGUboqD79" to tran_value / 3.0,
            "users/kVDZCSrD4UueSOq8bCzk" to tran_value / 3.0
        )

        TransactionHandler.createTransaction(
            groupReference = groupReferencePath!!,
            borrowers = b,
            title = binding.transactionTitle.text.toString(),
            category = "TEST CATEGORY",
            description = "yeet",
            lender = "users/kVDZCSrD4UueSOq8bCzk",
            value = tran_value / 3.0
        )
    }

    override fun onUserListFragmentInteraction(v: View, item: UserItem) {
        TODO("Not yet implemented")
    }
}
