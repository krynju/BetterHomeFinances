package com.example.betterhomefinances

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.MyGroupItemRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentGroupListBinding
import com.example.betterhomefinances.handlers.GroupHandler
import com.tsuryo.swipeablerv.SwipeLeftRightCallback


interface OnGroupListFragmentInteractionListener {


    fun onGroupListFragmentInteraction(v: View, item: String?)
}

class GroupListFragment : Fragment(), OnGroupListFragmentInteractionListener,
    SwipeLeftRightCallback.Listener {
    private val TAG = "ItemFragment.kt"

    private var navController: NavController? = null
    private var _binding: FragmentGroupListBinding? = null
    private val binding get() = _binding!!

    private var listener: OnGroupListFragmentInteractionListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGroupListBinding.inflate(inflater, container, false)

        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter =
            MyGroupItemRecyclerViewAdapter(
                GroupHandler,
                listener
            )
        binding.list.setListener(this)

        binding.button3.setOnClickListener { onCreateGroupClick(it) }

        navController = findNavController()
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    override fun onGroupListFragmentInteraction(v: View, item: String?) {
        val action = GroupListFragmentDirections.actionNavGroupsToNavGroupDetails(item!!)
        v.findNavController().navigate(action)
    }

    override fun onSwipedRight(position: Int) {
        val action = GroupListFragmentDirections.actionNavGroupsToNavCreateTransaction(
            GroupHandler.data[position].reference,
            null, 0.0F, null
        )
        navController?.navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TODO("Not yet implemented")
    }

    private fun onCreateGroupClick(v: View) {
        val action = GroupListFragmentDirections.actionNavGroupsToCreateGroup()
        findNavController().navigate(action)
    }
}
