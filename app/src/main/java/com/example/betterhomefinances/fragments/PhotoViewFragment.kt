package com.example.betterhomefinances.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.betterhomefinances.databinding.FragmentPhotoViewBinding
import com.example.betterhomefinances.handlers.StorageHandler
import java.io.File

class PhotoViewFragment : Fragment() {

    private var _binding: FragmentPhotoViewBinding? = null
    private val binding get() = _binding!!
    private lateinit var imagePath: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePath = arguments?.get("imagePath") as String

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPhotoViewBinding.inflate(inflater, container, false)
        val basePath =
            context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/"
        val currentPhotoPath =
            basePath + imagePath.split("/").last()
        if (!File(currentPhotoPath).exists()) {
            val ref = StorageHandler.mStorageRef!!.child(imagePath)
            ref.getFile(File(currentPhotoPath)).addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.imageView2.setImageBitmap(bitmap)
            }
        } else {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.imageView2.setImageBitmap(bitmap)
        }


        return binding.root
    }

}