package com.example.betterhomefinances.handlers

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.SetOptions


data class UserDetails(
    var settings: UserSettings? = null,
    var memberOfGroups: ArrayList<DocumentReference> = ArrayList<DocumentReference>()
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

    val userReference: DocumentReference get() = FirestoreHandler.users.document(userId.toString())


    fun initiateUserDetails() {
        val data = UserDetails(
            UserSettings("tempstuff"),
            ArrayList<DocumentReference>()
        )

        FirestoreHandler.users
            .document(userId.toString())
            .set(data, SetOptions.merge()) // TODO: this overwrites the config
            .addOnSuccessListener {
                Log.d(TAG, "Initiated userConfig record for $userId")
            }
    }

}