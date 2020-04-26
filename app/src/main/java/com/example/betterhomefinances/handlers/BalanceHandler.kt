package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp


data class Balance(
    var balances: HashMap<String, Double> = hashMapOf(),
    var paybacks: ArrayList<Triple<String, String, Double>> = arrayListOf(),
    var timestamp: Timestamp = Timestamp.now()
)

object BalanceHandler {


}