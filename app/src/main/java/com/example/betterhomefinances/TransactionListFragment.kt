package com.example.betterhomefinances

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.dummy.DummyContent
import com.example.betterhomefinances.dummy.DummyContent.DummyItem

interface OnTransactionListFragmentInteractionListener {

    // TODO: Update argument type and name
    fun onTransactionListFragmentInteraction(v: View, item: DummyItem?)
}

class TransactionListFragment : Fragment(), OnTransactionListFragmentInteractionListener {

    // TODO: Customize parameters
    private var columnCount = 1
    private val transactionReferencePath: String = "yeet"

    private var listenerTransaction: OnTransactionListFragmentInteractionListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyTransactionsRecyclerViewAdapter(DummyContent.ITEMS, listenerTransaction)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson
     * [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */


    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TransactionListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onTransactionListFragmentInteraction(v: View, item: DummyItem?) {
        val action =
            TransactionListFragmentDirections.actionTransactionsFragmentToTransactionDetails(
                transactionReferencePath
            )
        v.findNavController().navigate(action)
    }
}
