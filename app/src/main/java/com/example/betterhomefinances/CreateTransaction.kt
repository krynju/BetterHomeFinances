package com.example.betterhomefinances


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterhomefinances.adapters.UserItemRecyclerViewAdapter
import com.example.betterhomefinances.databinding.FragmentCreateTransactionBinding
import com.example.betterhomefinances.handlers.*
import com.google.firebase.firestore.DocumentReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.round


interface OnUserListFragmentInteractionListener {


    fun onUserListFragmentInteraction(v: View, item: UserItem)
}

class CreateTransaction : Fragment(), OnUserListFragmentInteractionListener {
    private var _binding: FragmentCreateTransactionBinding? = null
    private val binding get() = _binding!!
    var groupReferencePath: GroupReference? = null
    var transactionReferencePath: TransactionReference? = null
    private var mode: String = "create"
    private var loaner: String? = null
    private var value: Double = 0.0
    private lateinit var userAdapter: UserItemRecyclerViewAdapter
    var transactionStorage: TransactionStorage? = null
    var transaction: Transaction? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        groupReferencePath = arguments?.get("groupReferencePath") as GroupReference?
        val v = arguments?.get("value") as Float
        value = v.toDouble()
        loaner = arguments?.get("loaner") as String?
        transactionReferencePath =
            arguments?.get("transactionReferencePath") as TransactionReference?
        if (transactionReferencePath != null) {
            mode = "edit"
            transactionStorage = TransactionHandler.getInstance(groupReferencePath!!)
            transaction =
                transactionStorage!!.data.find { it.reference == transactionReferencePath }!!.transaction
        }

        userAdapter =
            UserItemRecyclerViewAdapter(
                groupReferencePath!!, transaction, value, loaner
            )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateTransactionBinding.inflate(inflater, container, false)
        binding.addButton.setOnClickListener { preTransaction(it) }
        binding.transactionValue.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
//                print("afte")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
//                print("before")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val ss = s.toString()
                val dd = ss.toDoubleOrNull()

                if (dd != null) {
                    userAdapter.distribute(dd)
                } else {
                    userAdapter.distribute(0.0)
                }
            }
        })
        binding.list.layoutManager = LinearLayoutManager(context)
        binding.list.adapter = userAdapter

        if (mode == "edit") {
            binding.transactionTitle.setText(transaction!!.title)
            binding.transactionDescription.setText(transaction!!.description)
            binding.transactionValue.setText(transaction!!.value.toString())
            if (transaction!!.imageReference != "") {
                currentPhotoPath =
                    context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + "/" + transaction!!.imageReference.split(
                        "/"
                    ).last()

                if (!File(currentPhotoPath).exists()) {
                    val ref = StorageHandler.mStorageRef!!.child(transaction!!.imageReference)
                    ref.getFile(File(currentPhotoPath)).addOnSuccessListener {
                        val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                        binding.image.setImageBitmap(bitmap)
                    }
                } else {
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    binding.image.setImageBitmap(bitmap)
                }
            }
        } else if (loaner != null) {
            binding.transactionTitle.setText("Payback")
            binding.transactionValue.setText((round(value * 100) / 100).toString())
        }
        binding.photoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
        return binding.root
    }

    fun preTransaction(v: View) {
        hideKeyboard()

        val tran_value = userAdapter.value
        if (tran_value == 0.0) return
        if (!userAdapter.indivitualValues.fold(true) { acc: Boolean, d: Double ->
                if (d < 0.0) {
                    acc.and(false); acc
                } else {
                    acc
                }
            }) {
            return
        }

        binding.addButton.isEnabled = false
        val a =
            userAdapter.indivitualValues.mapIndexed { index, d -> userAdapter.data[index].reference!! to d }
                .toMap()
        val b = HashMap(a)


        if (mode == "create") {
            TransactionHandler.createTransaction(
                groupReference = groupReferencePath!!,
                borrowers = b,
                title = binding.transactionTitle.text.toString(),
                category = "",
                description = binding.transactionDescription.text.toString(),
                lender = UserHandler.currentUserReference,
                value = tran_value
            ) {
                findNavController().navigateUp()
                handlePicture(it)
            }
        } else if (mode == "edit") {
            TransactionHandler.editTransaction(
                transactionReferencePath = transactionReferencePath!!,
                groupReference = groupReferencePath!!,
                value = tran_value,
                lender = transaction!!.lender!!,
                description = transaction!!.description,
                category = "",
                title = binding.transactionTitle.text.toString(),
                borrowers = b

            ) {
                findNavController().navigateUp()
                handlePicture(it)
            }
        }

    }

    fun handlePicture(transactionReference: DocumentReference) {
        if (mode == "edit" && transaction!!.imageReference.split("/")
                .last() == currentPhotoPath.split("/").last()
        ) {
            return
        } else if (this::currentPhotoPath.isInitialized) {
            val bitmap = (binding.image.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos)
            val data = baos.toByteArray()
            val p = "images/" + currentPhotoPath.split("/").last()
            val uploadTask = StorageHandler.mStorageRef!!.child(p)
            uploadTask.putBytes(data)
            transactionReference.update("imageReference", p)
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


    override fun onUserListFragmentInteraction(v: View, item: UserItem) {
        TODO("Not yet implemented")
    }

    val REQUEST_IMAGE_CAPTURE = 1


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == AppCompatActivity.RESULT_OK) {
            if (File(currentPhotoPath).exists()) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                binding.image.setImageBitmap(bitmap)
            }
        }
    }

    lateinit var currentPhotoPath: String

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity((activity as MainActivity).packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        (activity as MainActivity),
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            (activity as MainActivity).sendBroadcast(mediaScanIntent)
        }
    }
}
