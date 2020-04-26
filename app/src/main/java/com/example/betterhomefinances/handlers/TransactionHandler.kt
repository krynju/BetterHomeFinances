package com.example.betterhomefinances.handlers

import android.util.Log
import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.example.betterhomefinances.handlers.FirestoreHandler.groups
import com.example.betterhomefinances.handlers.FirestoreHandler.ref
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject

typealias TransactionReference = String

data class Transaction(
    val value: Double,
    val lender: UserReference,
    val timestamp: Timestamp,
    val borrowers: HashMap<UserReference, Double>,
    val title: String,
    val description: String,
    val category: String
)

object TransactionHandler {
    private val TAG = "TransactionHandler"
    fun transactionsReference(groupId: String) =
        groups.document(groupId).collection("transactions")

    fun transactionsReference(groupRef: DocumentReference) =
        groupRef.collection("transactions")


    fun createTransaction(
        groupReference: String,
        lender: String,
        title: String,
        value: Double,
        category: String,
        description: String,
        borrowers: HashMap<String, Double>
    ) {

        db.runTransaction { transaction ->
            val groupRef = ref(groupReference)
            val group = transaction.get(groupRef).toObject<Group>()

            val b = group?.balance?.balances

            if (b != null) {
                if (b.containsKey(lender)) {
                    b[lender] = value + b[lender]!!
                } else {
                    b[lender] = value
                }

                for (borrower in borrowers.keys) {
                    if (b.containsKey(borrower)) {
                        b[borrower] = b[borrower]!! - borrowers[borrower]!!
                    } else {
                        b[borrower] = -borrowers[borrower]!!
                    }
                }
            }

            group?.balance?.timestamp = Timestamp.now()
            if (b != null) {
                group.balance.balances = b
            }
            if (group != null) {
                transaction.set(groupRef, group)
            }
            transaction.set(
                transactionsReference(groupRef).document(),
                Transaction(value, lender, Timestamp.now(), borrowers, title, description, category)
            )
            null

        }.addOnSuccessListener { Log.d(TAG, "transaction done") }
            .addOnFailureListener { fail -> Log.d(TAG, "$fail") }

        // TODO: add paybacks calculation
    }

}