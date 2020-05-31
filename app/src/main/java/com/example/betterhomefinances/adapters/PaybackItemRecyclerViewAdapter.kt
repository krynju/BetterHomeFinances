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
import kotlinx.android.synthetic.main.fragment_payback_item.view.*
import kotlin.math.round

class PaybackItemRecyclerViewAdapter(groupReference: GroupReference) :
    RecyclerView.Adapter<PaybackItemRecyclerViewAdapter.ViewHolder>() {
    var data: ArrayList<Payback> = arrayListOf()
    var userNames: Map<UserReference, String> = mapOf()

    init {

        val group =
            GroupHandler.data.find { groupItem -> groupItem.reference == groupReference }!!.group
        val userIds = group.members.map { it.split("/")[1] }
        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener {
                userNames = (it.map {
                    it.reference.path to it.toObject<UserDetails>()!!.name!!
                }).toMap()
                notifyDataSetChanged()
            }


        GroupHandler.groupFromRef(groupReference)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->

                val g = documentSnapshot?.toObject<Group>()

                data = g!!.balance.paybacks
                notifyDataSetChanged()
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_payback_item, parent, false)
        return ViewHolder(view)
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mGroupNameView: TextView = mView.text_group_name
        val mBalanceView: TextView = mView.text_balance
        val mDescriptionView: TextView = mView.text_description


        override fun toString(): String {
            return super.toString() + " '" + mGroupNameView.text + "'"
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mGroupNameView.text = userNames[data[position].borrower]
        holder.mBalanceView.text = (round(data[position].value * 100) / 100).toString()
        holder.mDescriptionView.text = userNames[data[position].loaner]

    }
}