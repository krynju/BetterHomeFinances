package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.betterhomefinances.databinding.FragmentGroupDetailsBinding


class GroupDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var groupReferencePath: String? = null
    private var _binding: FragmentGroupDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as String?
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupDetailsBinding.inflate(inflater, container, false)
        binding.groupName.text = groupReferencePath
        binding.button4.setOnClickListener { createTransactionButton(it) }
        binding.button.setOnClickListener { goToTransactions(it) }
        return binding.root
    }

    fun goToTransactions(v: View) {
        val action = GroupDetailsFragmentDirections.actionNavGroupDetailsToTransactionsFragment(
            groupReferencePath!!
        )
        v.findNavController().navigate(action)
    }

    fun createTransactionButton(v: View) {
        val action = GroupDetailsFragmentDirections.actionNavGroupDetailsToCreateTransaction(
            groupReferencePath!!, null
        )
        v.findNavController().navigate(action)
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GroupDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("b", param1)
                    putString("A", param2)
                }
            }
    }
}
