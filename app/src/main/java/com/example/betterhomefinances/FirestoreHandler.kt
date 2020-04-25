package com.example.betterhomefinances

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreHandler {
    val db = Firebase.firestore

    var users = db.collection("users")
    var groups = db.collection("groups")


}