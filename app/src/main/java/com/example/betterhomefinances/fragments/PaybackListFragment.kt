package com.example.betterhomefinances.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.PaybackItemRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentPaybackListBinding
import com.example.betterhomefinances.handlers.GroupReference
import com.tsuryo.swipeablerv.SwipeLeftRightCallback


class PaybackListFragment : Fragment(), SwipeLeftRightCallback.Listener {
    private lateinit var groupReferencePath: String
    private val TAG = "ItemFragment.kt"
    lateinit var adapter: PaybackItemRecyclerViewAdapter

    private var navController: NavController? = null
    private var _binding: FragmentPaybackListBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as String

        adapter = PaybackItemRecyclerViewAdapter(groupReferencePath as GroupReference)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaybackListBinding.inflate(inflater, container, false)
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = adapter
        binding.list.setListener(this)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    override fun onSwipedRight(position: Int) {

        val action =
            PaybackListFragmentDirections.actionNavPaybacksToNavCreateTransaction(
                groupReferencePath,
                null,
                adapter.data[position].value.toFloat(),
                adapter.data[position].loaner
            )
        findNavController().navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TODO("Not yet implemented")
    }

}
