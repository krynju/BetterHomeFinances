package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp


data class Balance(
    var balances: HashMap<UserReference, Double> = hashMapOf(),
    var paybacks: ArrayList<Triple<UserReference, UserReference, Double>> = arrayListOf(),
    var timestamp: Timestamp = Timestamp.now()
)

object BalanceHandler {


}