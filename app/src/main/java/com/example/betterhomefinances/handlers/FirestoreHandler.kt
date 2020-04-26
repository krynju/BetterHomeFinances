package com.example.betterhomefinances.handlers

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object FirestoreHandler {
    val db = Firebase.firestore

    val users = db.collection("users")
    val groups = db.collection("groups")

    fun ref(ref: String) = db.document(ref)



}