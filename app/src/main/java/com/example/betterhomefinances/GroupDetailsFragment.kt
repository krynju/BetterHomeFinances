package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.BalanceItemRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentGroupDetailsBinding
import com.example.betterhomefinances.handlers.Group
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.GroupReference
import com.example.betterhomefinances.handlers.UserReference
import com.google.firebase.firestore.ktx.toObject


class GroupDetailsFragment : Fragment() {
    private var groupReferencePath: String? = null
    private lateinit var group: Group
    private var _binding: FragmentGroupDetailsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: BalanceItemRecyclerViewAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as String?
        group =
            GroupHandler.data.find { groupItem -> groupItem.reference == groupReferencePath }!!.group
        adapter = BalanceItemRecyclerViewAdapter(group)
    }


    fun updateGroupInfo() {
        binding.groupName.text = group.name
        binding.textView2.text = group.description
        adapter.data = (group.balance.balances.map {
            Pair(
                it.key,
                it.value
            )
        } as ArrayList<Pair<UserReference, Double>>)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupDetailsBinding.inflate(inflater, container, false)

        binding.button4.setOnClickListener { createTransactionButton(it) }
        binding.button.setOnClickListener { goToTransactions(it) }
        binding.paybacksButton.setOnClickListener { onClickPaybacks(it) }
        updateGroupInfo()
        GroupHandler.groupFromRef(groupReferencePath as GroupReference)
            .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                group = documentSnapshot!!.toObject<Group>()!!
                updateGroupInfo()
            }
        binding.list.layoutManager = LinearLayoutManager(context)

        binding.list.adapter = adapter

        return binding.root
    }

    private fun goToTransactions(v: View) {
        val action = GroupDetailsFragmentDirections.actionNavGroupDetailsToTransactionsFragment(
            groupReferencePath!!
        )
        v.findNavController().navigate(action)
    }

    private fun createTransactionButton(v: View) {
        val action = GroupDetailsFragmentDirections.actionNavGroupDetailsToCreateTransaction(
            groupReferencePath!!, null, 0.0F, null
        )
        v.findNavController().navigate(action)
    }

    fun onClickPaybacks(v: View) {
        val action =
            GroupDetailsFragmentDirections.actionNavGroupDetailsToNavPaybacks(groupReferencePath!!)
        findNavController().navigate(action)
    }


}
