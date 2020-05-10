package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp


data class Balance(
    var balances: HashMap<UserReference, Double> = hashMapOf(),
    var paybacks: ArrayList<Payback> = arrayListOf(),
    var timestamp: Timestamp = Timestamp.now()
)

data class Payback(
    var borrower: UserReference = "null",
    var loaner: UserReference = "null",
    val value: Double = 0.0
)

object BalanceHandler {


}