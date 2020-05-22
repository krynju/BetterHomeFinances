package com.example.betterhomefinances

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterhomefinances.handlers.GroupHandler

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [GroupListFragment.OnListFragmentInteractionListener] interface.
 */

interface OnGroupListFragmentInteractionListener {
    fun onGroupListFragmentInteraction(v: View, item: String?)
}

class GroupListFragment : Fragment(), OnGroupListFragmentInteractionListener {
    private val TAG = "ItemFragment.kt"
    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnGroupListFragmentInteractionListener = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_group_list, container, false)

        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                adapter = MyGroupItemRecyclerViewAdapter(GroupHandler, listener)
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }




    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            GroupListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onGroupListFragmentInteraction(v: View, item: String?) {
        val action = GroupListFragmentDirections.actionNavGroupsToNavGroupDetails(item!!)
        v.findNavController().navigate(action)
    }
}
