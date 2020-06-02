package com.example.betterhomefinances.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.R
import com.example.betterhomefinances.handlers.*
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_balance_item.view.*
import kotlin.math.round

class BalanceItemRecyclerViewAdapter(group: Group) :
    RecyclerView.Adapter<BalanceItemRecyclerViewAdapter.ViewHolder>() {
    var data: ArrayList<Pair<UserReference, Double>> = arrayListOf()
    var userNames: MutableMap<UserReference, String> = mutableMapOf()

    init {
//        userNames = data.map{""} as ArrayList<String>

        val userIds = group.members.map { it.split("/")[1] }
        val userRefs = group.members.map { it }

        val foundRefs = UserHandler.localUsersInfo.keys.filter { userRefs.contains(it) }
        userNames = foundRefs.map { it to UserHandler.localUsersInfo[it]!!.name!! }
            .toMap() as MutableMap<UserReference, String>

        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener {
                userNames = it.map {
                    it.reference.path to it.toObject<UserDetails>()!!.name!!
                }.toMap() as MutableMap<UserReference, String>

                UserHandler.localUsersInfo.putAll(it.map { it.reference.path to it.toObject<UserDetails>() })

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