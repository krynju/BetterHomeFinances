package com.example.betterhomefinances

import com.google.firebase.auth.FirebaseAuth

object UserHandler {
    var auth = FirebaseAuth.getInstance()

    val currentUser get() = auth.currentUser
    val userName get() = currentUser?.displayName
    val userId get() = currentUser?.uid


}