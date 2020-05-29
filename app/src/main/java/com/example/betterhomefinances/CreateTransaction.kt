package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.betterhomefinances.databinding.FragmentCreateTransactionBinding
import com.example.betterhomefinances.handlers.TransactionHandler
import com.example.betterhomefinances.handlers.UserHandler

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTransaction.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTransaction : Fragment() {
    // TODO: Rename and change types of parameters

    private var transactionName = "transaction_Name"

    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!
    var groupReferencePath: String? = null
    var transactionReferencePath: String? = null
    private var mode: String = "create"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as String?
        transactionReferencePath = arguments?.get("transactionReferencePath") as String?
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
        return binding.root
    }

    fun createTransaction(v: View) {
        val tran_value = binding.editText3.text.toString().toDoubleOrNull() ?: return

        val b = hashMapOf(
            UserHandler.currentUserReference.path to tran_value / 3.0,
            "users/uNMjYrRUfhDiGUboqD79" to tran_value / 3.0,
            "users/kVDZCSrD4UueSOq8bCzk" to tran_value / 3.0
        )

        TransactionHandler.createTransaction(
            groupReference = groupReferencePath!!,
            borrowers = b,
            title = binding.editText.text.toString(),
            category = "TEST CATEGORY",
            description = "yeet",
            lender = "users/kVDZCSrD4UueSOq8bCzk",
            value = tran_value / 3.0
        )
    }
}
