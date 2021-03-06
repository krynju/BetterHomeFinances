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
import com.example.betterhomefinances.databinding.FragmentCreateGroupBinding
import com.example.betterhomefinances.handlers.GroupHandler


class CreateGroupFragment : Fragment() {
    private var _binding: FragmentCreateGroupBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGroupBinding.inflate(inflater, container, false)
        binding.button3.setOnClickListener { onClickCreateGroup(it) }
        return binding.root
    }

    fun onClickCreateGroup(v: View) {
        hideKeyboard()
        val name = binding.groupname.text!!
        val desc = binding.descriptionname.text!!


        if (name.isEmpty() && desc.isEmpty()) {
            binding.outlinedTextField.error = "Please input the name"
            binding.outlinedTextField2.error = "Please input the description"
            return
        }
        if (name.isEmpty()) {
            binding.outlinedTextField.error = "Please input the name"
            return
        }
        if (desc.isEmpty()) {
            binding.outlinedTextField2.error = "Please input the description"
            return
        }


        if (name.length > 25) {
            binding.outlinedTextField.error = "Group name is too long"
            return
        }

        binding.button3.isEnabled = false
        binding.outlinedTextField.isEnabled = false
        binding.outlinedTextField2.isEnabled = false

        GroupHandler.createGroup(name.toString(), desc.toString()) {
            print(it)

            findNavController().navigateUp()
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
