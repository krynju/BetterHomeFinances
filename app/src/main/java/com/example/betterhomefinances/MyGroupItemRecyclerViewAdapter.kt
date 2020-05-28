package com.example.betterhomefinances


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.GroupItem
import com.example.betterhomefinances.handlers.UserHandler
import kotlinx.android.synthetic.main.fragment_group_item.view.*
import kotlin.math.round

/**
 * [RecyclerView.Adapter] that can display a [GroupItemDUMMYTOREMOVE] and makes a call to the
 * specified [OnGroupListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyGroupItemRecyclerViewAdapter(
    private val contentHandler: GroupHandler,
    private val mListener: OnGroupListFragmentInteractionListener
) : RecyclerView.Adapter<MyGroupItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var mOnListChangedCallback: OnListChangedCallback<ObservableList<GroupItem>>

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as GroupItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener.onGroupListFragmentInteraction(v, item.reference)
        }

        mOnListChangedCallback = MyOnListChangedCallback(this)

        contentHandler.data.addOnListChangedCallback(mOnListChangedCallback)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = contentHandler.data[position]
        holder.mGroupNameView.text = item.group.name

        val balance = item.group.balance.balances[UserHandler.currentUserReference.path]!!
        val rounded = (round(balance * 100) / 100)
        val prefix = if (rounded > 0) {
            "+"
        } else {
            "-"
        }

        holder.mBalanceView.text = prefix + rounded.toString()
        if (rounded > 0) {
            holder.mBalanceView.setTextColor(holder.mView.resources.getColor(R.color.secondaryGreen))
        } else {
            holder.mBalanceView.setTextColor(holder.mView.resources.getColor(R.color.secondaryRed))
        }
        holder.mDescriptionView.text = item.group.name
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = contentHandler.data.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mGroupNameView: TextView = mView.text_group_name
        val mBalanceView: TextView = mView.text_balance
        val mDescriptionView: TextView = mView.text_description


        override fun toString(): String {
            return super.toString() + " '" + mGroupNameView.text + "'"
        }
    }
}

private class MyOnListChangedCallback(myGroupItemRecyclerViewAdapter: MyGroupItemRecyclerViewAdapter) :
    ObservableList.OnListChangedCallback<ObservableList<GroupItem>>(
    ) {
    var MTEST: MyGroupItemRecyclerViewAdapter = myGroupItemRecyclerViewAdapter
    override fun onChanged(sender: ObservableList<GroupItem>?) {
//        TODO("Not yet implemented")
        MTEST.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.notifyDataSetChanged()

    }

    override fun onItemRangeMoved(
        sender: ObservableList<GroupItem>?,
        fromPosition: Int,
        toPosition: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.notifyDataSetChanged()

    }

    override fun onItemRangeInserted(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.notifyDataSetChanged()

    }

    override fun onItemRangeChanged(
        sender: ObservableList<GroupItem>?,
        positionStart: Int,
        itemCount: Int
    ) {
//        TODO("Not yet implemented")
        MTEST.notifyDataSetChanged()

    }

}



