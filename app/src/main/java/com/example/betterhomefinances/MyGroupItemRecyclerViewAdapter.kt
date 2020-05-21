package com.example.betterhomefinances


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.ObservableList
import androidx.databinding.ObservableList.OnListChangedCallback
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.GroupListFragment.OnListFragmentInteractionListener
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.GroupItem
import kotlinx.android.synthetic.main.fragment_group_item.view.*

/**
 * [RecyclerView.Adapter] that can display a [GroupItemDUMMYTOREMOVE] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class MyGroupItemRecyclerViewAdapter(
    private val contentHandler: GroupHandler,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<MyGroupItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener
    private var mOnListChangedCallback: OnListChangedCallback<ObservableList<GroupItem>>


    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as GroupItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onGroupListFragmentInteraction(v, item.reference)
        }

        mOnListChangedCallback = MyOnListChangedCallback(this)

        GroupHandler.data.addOnListChangedCallback(mOnListChangedCallback)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_group_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = GroupHandler.data[position]
        holder.mIdView.text = item.group.name
        holder.mContentView.text = item.group.name

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = GroupHandler.data.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.item_number
        val mContentView: TextView = mView.content


        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
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



