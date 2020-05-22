package com.example.betterhomefinances.handlers

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.example.betterhomefinances.handlers.FirestoreHandler.ref
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.toObject
import kotlin.math.abs

typealias TransactionReference = String

data class Transaction(
    val value: Double = 0.0,
    val lender: UserReference? = null,
    val timestamp: Timestamp? = null,
    val borrowers: HashMap<UserReference, Double> = hashMapOf(),
    val title: String = "",
    val description: String = "",
    val category: String = ""
)

data class TransactionItem(
    val reference: TransactionReference,
    val transaction: Transaction
)


object TransactionHandler {
    private val TAG = "TransactionHandler"

    //    fun transactionsReference(groupId: String) =
//        groups.document(groupId).collection("transactions")
//
//
    fun transactionsReference(groupId: GroupReference) =
        db.document(groupId).collection("transactions")



    fun transactionsReference(groupRef: DocumentReference) =
        groupRef.collection("transactions")


    var storage: HashMap<GroupReference, TransactionStorage> = hashMapOf()

    fun getInstance(groupRefPath: GroupReference): TransactionStorage {
        return if (storage.containsKey(groupRefPath)) {
            storage[groupRefPath]!!
        } else {
            storage[groupRefPath] = TransactionStorage(groupRefPath)
            storage[groupRefPath]!!
        }
    }


    fun getTransactionsRefPair(
        groupRefPath: GroupReference,
        callback: (List<TransactionItem>) -> Unit
    ) {
        TransactionHandler.transactionsReference(groupRefPath).get()
            .addOnSuccessListener { result ->
                callback(result.map {
                    TransactionItem(
                        it.reference.path,
                        it.toObject<Transaction>()
                    )
                })
            }
    }


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
            val group = transaction.get(groupRef).toObject<Group>()!!

            val b = group.balance.balances
            val p = ArrayList<Payback>()


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

            val bb = b.toMutableMap()
            val positiveKeys =
                bb.filterValues { it >= 0.01 }.toList().sortedByDescending { it.second }
                    .map { it.first }
            val negativeKeys =
                bb.filterValues { it <= -0.01 }.toList().sortedBy { it.second }.map { it.first }

            for (pKey in positiveKeys) {
                for (nKey in negativeKeys) {
                    val pos = bb[pKey]!!
                    val neg = bb[nKey]!!
                    if (abs(neg) >= 0.01) {
                        if (pos >= abs(neg)) {
                            bb[pKey] = pos + neg
                            bb[nKey] = 0.0
                            p.add(Payback(nKey, pKey, abs(neg)))
                        } else {
                            bb[nKey] = neg + pos
                            bb[pKey] = 0.0
                            p.add(Payback(nKey, pKey, pos))
                        }
                    }
                }
            }
            group.balance.paybacks = p
            group.balance.timestamp = Timestamp.now()
            group.balance.balances = b

            transaction.set(groupRef, group)

            transaction.set(
                transactionsReference(groupRef).document(),
                Transaction(value, lender, Timestamp.now(), borrowers, title, description, category)
            )
            null
        }.addOnSuccessListener { Log.d(TAG, "transaction done") }
            .addOnFailureListener { fail -> Log.d(TAG, "$fail") }
    }

}


class TransactionStorage(groupRefPath: GroupReference) {
    var data: ObservableList<TransactionItem> = ObservableArrayList<TransactionItem>()

    init {
        TransactionHandler.getTransactionsRefPair(groupRefPath) {
            data.addAll(it)
        }
    }
}

