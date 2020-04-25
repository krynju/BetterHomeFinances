package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Transaction(
    val value: Double,
    val timestamp: Timestamp,
    val borrowers: HashMap<DocumentReference, Double>,
    val title: String,
    val description: String,
    val category: String
)

object TransactionHandler {
    fun transactionsReference(groupId: String) =
        FirestoreHandler.groups.document(groupId).collection("transactions")

    fun transactionsReference(groupRef: DocumentReference) =
        groupRef.collection("transactions")

}