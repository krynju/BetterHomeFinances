package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.databinding.FragmentHomeBinding
import com.example.betterhomefinances.handlers.GroupHandler
import com.example.betterhomefinances.handlers.UserDetails
import com.example.betterhomefinances.handlers.UserHandler

class HomeFragment : Fragment(), OnGroupListFragmentInteractionListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var temptext: UserDetails? = null;
    private var listener = this

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

        binding.textView2.text = UserHandler.userName;
        binding.textView3.text = UserHandler.userId;

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
}
