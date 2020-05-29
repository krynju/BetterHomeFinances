package com.example.betterhomefinances


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.databinding.FragmentUserItemBinding
import com.example.betterhomefinances.handlers.*
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.ktx.toObject
import kotlinx.android.synthetic.main.fragment_user_item.view.*


class MyUsersRecyclerViewAdapter(
    private val groupReference: GroupReference
) : RecyclerView.Adapter<MyUsersRecyclerViewAdapter.ViewHolder>() {
    private var groupItem: GroupItem = GroupHandler.data.find { it.reference == groupReference }!!
    private val mOnClickListener: View.OnClickListener
    private var data: List<UserItem> = listOf()

    private var switchList: ArrayList<Boolean> = arrayListOf()
    private var fixedList: ArrayList<Boolean> = arrayListOf()


    init {
        val userIds = groupItem.group.members.map { it.split("/")[1] }
        FirestoreHandler.users.whereIn(FieldPath.documentId(), userIds)
            .get()
            .addOnSuccessListener {
                data = it.map {
                    UserItem(it.reference.path, it.toObject<UserDetails>())
                }
                switchList = it.map { false } as ArrayList<Boolean>
                fixedList = it.map { false } as ArrayList<Boolean>
                notifyDataSetChanged()
            }

        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as UserItem
//            mListenerTransaction.onUserListFragmentInteraction(v, item)
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            FragmentUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding.root)
    }

    fun updateSwitchList(position: Int, isChecked: Boolean) {

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.userName.text = item.user!!.name.toString()
        holder.valueBorrowed.setText("0.0")
        holder.pos = position
//        holder.radioButton.isChecked = switchList[position]
//        holder.switchButton.isActivated = fixedList[position]
        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }

    }


    override fun getItemCount(): Int = data.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val userName: TextView = mView.text_group_name
        val valueBorrowed: TextInputEditText = mView.u_val_n
        val switchButton = mView.switch_fix
        val radioButton = mView.radio_button
        var pos: Int = 0


        override fun toString(): String {
            return super.toString() + " '" + userName.text + "'"
        }
    }
}
