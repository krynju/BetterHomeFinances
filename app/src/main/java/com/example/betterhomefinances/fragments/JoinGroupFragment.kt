package com.example.betterhomefinances.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.betterhomefinances.databinding.FragmentSlideshowBinding
import com.example.betterhomefinances.handlers.GroupHandler

class JoinGroupFragment : Fragment() {
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
        hideKeyboard()
        val ss = binding.groupname.text.toString()
        if (ss != "") {
            binding.joinButton.isEnabled = false
            GroupHandler.joinGroup(ss,
                { findNavController().navigateUp() },
                { binding.joinButton.isEnabled = true })
        }

    }

    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
