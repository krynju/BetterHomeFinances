package com.example.betterhomefinances.adapters


import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.databinding.FragmentUserItemBinding
import com.example.betterhomefinances.handlers.*
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_user_item.view.*
import kotlin.math.round


class UserItemRecyclerViewAdapter(
    private val groupReference: GroupReference,
    transaction: Transaction?,
    toDouble: Double,
    loaner: String?
) : RecyclerView.Adapter<UserItemRecyclerViewAdapter.ViewHolder>() {
    private var groupItem: GroupItem = GroupHandler.data.find { it.reference == groupReference }!!
    var data: List<UserItem> = listOf()

    private var switchList: ArrayList<Boolean> = arrayListOf()
    private var radioList: ArrayList<Boolean> = arrayListOf()
    var indivitualValues: ArrayList<Double> = arrayListOf()
    var value: Double = 0.0

    init {
        val userIds = groupItem.group.members.map { it.split("/")[1] }
        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener {
                data = it.map {
                    UserItem(it.reference.path, it.toObject<UserDetails>())
                }

                indivitualValues = it.map { 0.0 } as ArrayList<Double>
                switchList = it.map { false } as ArrayList<Boolean>
//                    radioList = it.map { false } as ArrayList<Boolean>
//                    radioList[data.indexOfFirst { it.reference == UserHandler.currentUserReference }] =
//                        true
                if (transaction != null) {
                    for (u in data.indices) {
                        if (transaction.borrowers.containsKey(data[u].reference)) {
                            indivitualValues[u] = transaction.borrowers[data[u].reference]!!
                        }
                        switchList[u] = true
                    }
                } else if (loaner != null) {
                    val ind = data.indexOfFirst { it.reference == loaner }
                    indivitualValues[ind] = toDouble
                    value = toDouble
                    switchList[ind] = true
                }
                notifyDataSetChanged()
            }

//        mOnClickListener = View.OnClickListener { v ->
//            val item = v.tag as UserItem
////            mListenerTransaction.onUserListFragmentInteraction(v, item)
//        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FragmentUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        val itemValue = indivitualValues[position]
        holder.userName.text = item.user!!.name.toString()
        holder.valueBorrowed.setText((round(indivitualValues[position] * 100) / 100).toString())
        holder.switchButton.isChecked = switchList[position]

        holder.valueBorrowed.isEnabled = switchList[position]

        holder.switchButton.setOnCheckedChangeListener { buttonView, isChecked ->

            val prev = switchList[position]
            if (prev != isChecked) {
                switchList[position] = isChecked
                holder.valueBorrowed.isEnabled = isChecked

                if (!isChecked && prev) {
                    distribute(value)
                }
            }
        }

        holder.valueBorrowed.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                TODO("Not yet implemented")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ss = s.toString()
                val dd = ss.toDoubleOrNull()

                if (holder.valueBorrowed.hasFocus() && itemValue != dd) {
//                    holder.switchButton.isActivated = true
//                    switchList[position] = true

                    if (dd != null) {
                        indivitualValues[position] = dd
                        distribute(value)
                    } else {
                        indivitualValues[position] = 0.0
                        distribute(value)
//                    userAdapter.clearIndividualValues()
                    }
                }
            }
        })
    }


    fun distribute(d: Double) {
        value = d
        val unfixedCount = switchList.fold(0) { acc: Int, b: Boolean ->
            if (b) {
                acc
            } else {
                acc + 1
            }
        }

        val fixedValue = indivitualValues.foldIndexed(0.0) { index, sum, el ->
            if (switchList[index]) {
                sum + el
            } else {
                sum
            }
        }
        val distributedValue = (d - fixedValue) / unfixedCount
        for (i in indivitualValues.indices) {
            if (!switchList[i] && indivitualValues[i] != distributedValue) {
                indivitualValues[i] = distributedValue
                Handler().post(Runnable { notifyItemChanged(i) })
            }
        }
    }



    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val userName: TextView = mView.text_group_name
        val valueBorrowed: TextInputEditText = mView.u_val_n
        val switchButton: SwitchMaterial = mView.switch_fix
    }
}
