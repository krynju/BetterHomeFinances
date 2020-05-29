package com.example.betterhomefinances.handlers

import android.util.Log
import androidx.databinding.ObservableArrayMap
import androidx.databinding.ObservableMap
import com.example.betterhomefinances.handlers.FirestoreHandler.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

typealias UserReference = String

data class UserDetails(
    var name: String? = null,
    var settings: UserSettings? = UserSettings(),
    var memberOfGroups: ArrayList<GroupReference> = ArrayList()
)

data class UserSettings(
    var tempstuff: String? = null
)

data class UserItem(
    var reference: UserReference? = null,
    var user: UserDetails? = null

)

object UserHandler {
    private const val TAG = "UserHandler.kt"
    private val auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser
    val userName: String? get() = currentUser?.displayName
    private val userId: String? get() = currentUser?.uid

    init {
        getUserDetails(currentUserDocumentReference, {
            userDetails[currentUserDocumentReference.path] = it
        }, {})
    }

//    val users get()= FirestoreHandler.db.collection("")

    val currentUserReference: UserReference get() = currentUserDocumentReference.path
    var userDetails: ObservableMap<UserReference, UserDetails> = ObservableArrayMap()

    val currentUserDocumentReference: DocumentReference get() = users.document(userId.toString())


    fun getUserDetails(
        userReference: DocumentReference,
        callback: (UserDetails) -> Unit,
        failureCallback: () -> Unit
    ) =
        userReference
            .get()
            .addOnSuccessListener { result ->
                result.toObject<UserDetails>()?.let { callback(it) }
            }
            .addOnFailureListener { failureCallback() }

    fun initiateUserDetails(callback: () -> Unit) {
        val data = UserDetails(
            userName,
            UserSettings("tempstuff"),
            ArrayList<String>()
        )

        users
            .document(userId.toString())
            .set(data, SetOptions.merge()) // TODO: this overwrites the config
            .addOnSuccessListener {
                Log.d(TAG, "Initiated userConfig record for $userId")
                callback()
            }
    }

}