package com.example.betterhomefinances.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.R
import com.example.betterhomefinances.handlers.FirestoreHandler
import com.example.betterhomefinances.handlers.Group
import com.example.betterhomefinances.handlers.UserDetails
import com.example.betterhomefinances.handlers.UserReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_balance_item.view.*
import kotlin.math.round

class BalanceItemRecyclerViewAdapter(group: Group) :
    RecyclerView.Adapter<BalanceItemRecyclerViewAdapter.ViewHolder>() {
    var data: ArrayList<Pair<UserReference, Double>> = arrayListOf()
    var userNames: Map<UserReference, String> = mapOf()

    init {
//        userNames = data.map{""} as ArrayList<String>

        val userIds = group.members.map { it.split("/")[1] }
        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener {
                userNames = (it.map {
                    it.reference.path to it.toObject<UserDetails>()!!.name!!
                }).toMap()

                data.clear()
                data.addAll(group.balance.balances.map { Pair(it.key, it.value) })

                notifyDataSetChanged()
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_balance_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mGroupNameView: TextView = mView.text_group_name
        val mBalanceView: TextView = mView.text_balance
//        val mDescriptionView: TextView = mView.text_description


        override fun toString(): String {
            return super.toString() + " '" + mGroupNameView.text + "'"
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mGroupNameView.text = userNames[data[position].first]
        holder.mBalanceView.text = (round(data[position].second * 100) / 100).toString()

    }
}