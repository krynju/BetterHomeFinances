package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.databinding.FragmentHomeBinding
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.UserDetails
import com.example.betterhomefinances.handlers.UserHandler
import com.tsuryo.swipeablerv.SwipeLeftRightCallback

class HomeFragment : Fragment(), OnGroupListFragmentInteractionListener,
    SwipeLeftRightCallback.Listener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var temptext: UserDetails? = null;
    private var listener = this
    private var navController: NavController? = null;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        UserHandler.userDetails(
            fun(u: UserDetails) {
                temptext = u
                binding.textHome.text = temptext?.settings?.tempstuff!!
            },
            fun() {
                UserHandler.initiateUserDetails()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = MyGroupItemRecyclerViewAdapter(GroupHandler, listener)
        binding.recyclerView.setListener(this)

        binding.textView2.text = UserHandler.userName;
        binding.textView3.text = UserHandler.userId;

        navController = findNavController()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onGroupListFragmentInteraction(v: View, item: String?) {
        val action = HomeFragmentDirections.actionNavHomeToNavGroupDetails(item!!)
        v.findNavController().navigate(action)
    }

    override fun onSwipedRight(position: Int) {
        val action = HomeFragmentDirections.actionNavHomeToCreateTransaction(
            GroupHandler.data[position].reference,
            null
        )
        navController?.navigate(action)
    }

    override fun onSwipedLeft(position: Int) {
        TODO("Not yet implemented")
    }
}

