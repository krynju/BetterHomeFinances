package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.betterhomefinances.databinding.FragmentGroupDetailsBinding
import com.example.betterhomefinances.handlers.Group
import com.example.betterhomefinances.handlers.GroupHandler


class GroupDetailsFragment : Fragment() {
    private var groupReferencePath: String? = null
    private lateinit var group: Group
    private var _binding: FragmentGroupDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as String?
        group =
            GroupHandler.data.find { groupItem -> groupItem.reference == groupReferencePath }!!.group
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupDetailsBinding.inflate(inflater, container, false)
        binding.groupName.text = group.name
        binding.textView2.text = group.description
        binding.button4.setOnClickListener { createTransactionButton(it) }
        binding.button.setOnClickListener { goToTransactions(it) }
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
            groupReferencePath!!, null
        )
        v.findNavController().navigate(action)
    }


}
