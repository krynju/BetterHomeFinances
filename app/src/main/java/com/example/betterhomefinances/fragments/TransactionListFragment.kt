package com.example.betterhomefinances.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.MyTransactionsRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentTransactionListBinding
import com.example.betterhomefinances.handlers.GroupReference
import com.example.betterhomefinances.handlers.TransactionHandler
import com.example.betterhomefinances.handlers.TransactionItem
import com.example.betterhomefinances.handlers.TransactionStorage
import com.tsuryo.swipeablerv.SwipeLeftRightCallback

interface OnTransactionListFragmentInteractionListener {

    // TODO: Update argument type and name
    fun onTransactionListFragmentInteraction(v: View, item: TransactionItem)
}

class TransactionListFragment : Fragment(),
    OnTransactionListFragmentInteractionListener,
    SwipeLeftRightCallback.Listener {
    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!


    private var _groupReferencePath: GroupReference? = null
    private val groupReferencePath get() = _groupReferencePath!!

    private var listenerTransaction: OnTransactionListFragmentInteractionListener = this
    private var _transactionHandler: TransactionStorage? = null
    private val transactionHandler get() = _transactionHandler!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _groupReferencePath = arguments?.get("groupReferencePath") as String?
        _transactionHandler = TransactionHandler.getInstance(groupReferencePath)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)


        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter =
            MyTransactionsRecyclerViewAdapter(
                transactionHandler,
                listenerTransaction
            )

        binding.list.setListener(this)
        return binding.root
    }

    override fun onTransactionListFragmentInteraction(v: View, item: TransactionItem) {
        val action =
            TransactionListFragmentDirections.actionTransactionsFragmentToTransactionDetails(
                item.reference.toString(),
                groupReferencePath
            )
        v.findNavController().navigate(action)
    }

    override fun onSwipedRight(position: Int) {
        // edit
        val action =
            TransactionListFragmentDirections.actionNavTransactionListFragmentToNavCreateTransaction(
                groupReferencePath,
                transactionHandler.data[position]?.reference, 0.0F, null
            )
        findNavController().navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TransactionHandler.deleteTransaction(
            transactionHandler.data[position]?.reference!!,
            groupReferencePath
        ) {
            val a = binding.list.adapter!!
//            transactionHandler.data.removeAt(position)
//            a.notifyItemRemoved(position)
//            a.notifyItemRemoved(a.itemCount)
        }
    }
}
