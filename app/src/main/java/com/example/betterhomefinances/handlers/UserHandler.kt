package com.example.betterhomefinances.handlers

import android.util.Log
import com.example.betterhomefinances.handlers.FirestoreHandler.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject

typealias UserReference = String

data class UserDetails(
    var settings: UserSettings? = UserSettings(),
    var memberOfGroups: ArrayList<GroupReference> = ArrayList()
)

data class UserSettings(
    var tempstuff: String? = null
)

object UserHandler {
    private const val TAG = "UserHandler.kt"
    private val auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser
    val userName: String? get() = currentUser?.displayName
    val userId: String? get() = currentUser?.uid

    val currentUserReference: DocumentReference get() = users.document(userId.toString())

    fun userDetails(callback: (UserDetails) -> Unit, failureCallback: () -> Unit) =
        currentUserReference
        .get()
            .addOnSuccessListener { result ->
                result.toObject<UserDetails>()?.let { callback(it) }
            }
        .addOnFailureListener { failureCallback() }

    fun initiateUserDetails() {
        val data = UserDetails(
            UserSettings("tempstuff"),
            ArrayList<String>()
        )

        users
            .document(userId.toString())
            .set(data, SetOptions.merge()) // TODO: this overwrites the config
            .addOnSuccessListener {
                Log.d(TAG, "Initiated userConfig record for $userId")
            }
    }

}