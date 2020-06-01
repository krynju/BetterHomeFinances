package com.example.betterhomefinances.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.R
import com.example.betterhomefinances.handlers.UserDetails
import com.example.betterhomefinances.handlers.UserReference
import kotlinx.android.synthetic.main.fragment_borrower_item.view.*
import kotlin.math.round

class BorrowersListRecyclerViewAdapter :
    RecyclerView.Adapter<BorrowersListRecyclerViewAdapter.ViewHolder>() {

    var data: ArrayList<Pair<UserReference, Double>> = arrayListOf()
    lateinit var userNames: MutableMap<UserReference, UserDetails>

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BorrowersListRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_borrower_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mIdView.text = userNames[data[position].first]!!.name
        holder.mContentView.text = (round(data[position].second * 100) / 100).toString()
    }


    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mIdView: TextView = mView.text_group_name
        val mContentView: TextView = mView.u_val_nn
    }
}
