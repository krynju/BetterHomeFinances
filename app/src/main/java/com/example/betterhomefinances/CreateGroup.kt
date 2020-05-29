package com.example.betterhomefinances

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.betterhomefinances.databinding.FragmentCreateGroupBinding
import com.example.betterhomefinances.handlers.GroupHandler


class CreateGroup : Fragment() {
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
        val name = binding.groupname.text!!
        val desc = binding.descriptionname.text!!


        if (name.length == 0 && desc.length == 0) {
            binding.outlinedTextField.error = "Please input the name"
            binding.outlinedTextField2.error = "Please input the description"
            return
        }
        if (name.length == 0) {
            binding.outlinedTextField.error = "Please input the name"
            return
        }
        if (desc.length == 0) {
            binding.outlinedTextField2.error = "Please input the description"
            return
        }


        if (name.length > 25) {
            binding.outlinedTextField.error = "Group name is too long"
            return
        }

        GroupHandler.createGroup(name.toString(), desc.toString()) { print(it) }




    }
}
