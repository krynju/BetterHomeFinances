package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.betterhomefinances.databinding.FragmentSlideshowBinding
import com.example.betterhomefinances.handlers.GroupHandler

class SlideshowFragment : Fragment() {
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        binding.joinButton.setOnClickListener { onClickJoin() }
        return binding.root
    }

    fun onClickJoin() {
        val ss = binding.groupname.text.toString()
        if (ss != "") {
            binding.joinButton.isEnabled = false
            GroupHandler.joinGroup(ss,
                { findNavController().navigateUp() },
                { binding.joinButton.isEnabled = true })
        }

    }
}
