package com.example.betterhomefinances.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.OnTransactionListFragmentInteractionListener
import com.example.betterhomefinances.R
import com.example.betterhomefinances.handlers.TransactionItem
import com.example.betterhomefinances.handlers.TransactionStorage
import kotlinx.android.synthetic.main.fragment_transaction_item.view.*
import kotlin.math.round

/**
 * [RecyclerView.Adapter] that can display a [DummyItem] and makes a call to the
 * specified [OnTransactionListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyTransactionsRecyclerViewAdapter(
    private val contentHandler: TransactionStorage,
    private val mListenerTransaction: OnTransactionListFragmentInteractionListener?
) : RecyclerView.Adapter<MyTransactionsRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private val mOnListChangeCallback: ObservableList.OnListChangedCallback<ObservableList<TransactionItem>>
    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as TransactionItem
            mListenerTransaction?.onTransactionListFragmentInteraction(v, item)
        }

        mOnListChangeCallback =
            MyTransactionOnListChangedCallback(
                this
            )
        contentHandler.data.addOnListChangedCallback(mOnListChangeCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_transaction_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contentHandler.data[position]
        holder.mIdView.text = item.transaction?.title
        holder.mContentView.text = item.transaction!!.timestamp!!.toDate().toString()
        holder.mVal.text = (round(item.transaction.value * 100) / 100).toString()


        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = contentHandler.data.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.text_group_name
        val mContentView: TextView = mView.text_description
        val mVal: TextView = mView.text_balance

    }
}


private class MyTransactionOnListChangedCallback(myTransactionsRecyclerViewAdapter: MyTransactionsRecyclerViewAdapter) :
    ObservableList.OnListChangedCallback<ObservableList<TransactionItem>>(
    ) {
    var MTEST: MyTransactionsRecyclerViewAdapter = myTransactionsRecyclerViewAdapter
    override fun onChanged(sender: ObservableList<TransactionItem>?) {
        MTEST.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<TransactionItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
        MTEST.notifyDataSetChanged()
    }

    override fun onItemRangeMoved(
        sender: ObservableList<TransactionItem>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
        MTEST.notifyDataSetChanged()
    }

    override fun onItemRangeInserted(
        sender: ObservableList<TransactionItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
        MTEST.notifyDataSetChanged()
    }

    override fun onItemRangeChanged(
        sender: ObservableList<TransactionItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
        MTEST.notifyDataSetChanged()
    }

}