package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

data class Balance(
    var balances: HashMap<DocumentReference, Double>,
    var paybacks: ArrayList<Triple<DocumentReference, DocumentReference, Double>>,
    val timestamp: Timestamp
)

object BalanceHandler {
}