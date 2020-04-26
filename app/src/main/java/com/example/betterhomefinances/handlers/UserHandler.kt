package com.example.betterhomefinances.handlers

import android.util.Log
import com.example.betterhomefinances.handlers.FirestoreHandler.users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions


data class UserDetails(
    var settings: UserSettings? = null,
    var memberOfGroups: ArrayList<String> = ArrayList<String>()
)

data class UserSettings(
    var tempstuff: String? = null
)

object UserHandler {
    private val TAG = "UserHandler.kt"
    var auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser
    val userName: String? get() = currentUser?.displayName
    val userId: String? get() = currentUser?.uid

    val userReference: DocumentReference get() = users.document(userId.toString())


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