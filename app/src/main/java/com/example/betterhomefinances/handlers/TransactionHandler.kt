package com.example.betterhomefinances.handlers

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.example.betterhomefinances.handlers.FirestoreHandler.ref
import com.example.betterhomefinances.handlers.UserHandler.currentUserReference
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlin.math.abs

typealias TransactionReference = String

data class Transaction(
    var value: Double = 0.0,
    var creator: UserReference? = null,
    var lender: UserReference? = null,
    val timestamp: Timestamp? = null,
    var borrowers: HashMap<UserReference, Double> = hashMapOf(),
    var title: String = "",
    var description: String = "",
    var category: String = ""
)

data class TransactionItem(
    val reference: TransactionReference? = null,
    val transaction: Transaction? = null
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
        groupReference: GroupReference,
        lender: UserReference,
        title: String,
        value: Double,
        category: String,
        description: String,
        borrowers: HashMap<UserReference, Double>, successCallback: () -> Unit
    ) {
        db.runTransaction { dbTransaction ->
            val groupRef = ref(groupReference)
            val group = dbTransaction.get(groupRef).toObject<Group>()!!


            group.balance.balances =
                generateBalances(group.balance.balances, lender, borrowers, value)
            group.balance.paybacks = generatePaybackList(group.balance.balances)
            group.balance.timestamp = Timestamp.now()

            dbTransaction.set(groupRef, group)

            dbTransaction.set(
                transactionsReference(groupRef).document(),
                Transaction(
                    value,
                    lender,
                    currentUserReference,
                    Timestamp.now(),
                    borrowers,
                    title,
                    description,
                    category
                )
            )
            null
        }.addOnSuccessListener { Log.d(TAG, "transaction done"); successCallback() }
            .addOnFailureListener { fail -> Log.d(TAG, "$fail") }
    }


    fun editTransaction(
        transactionReferencePath: TransactionReference,
        groupReference: GroupReference,
        lender: UserReference,
        title: String,
        value: Double,
        category: String,
        description: String,
        borrowers: HashMap<UserReference, Double>,
        callback: () -> Unit
    ) {
        db.runTransaction { dbTransaction ->
            val transactionReference = ref(transactionReferencePath)
            val transaction = dbTransaction.get(transactionReference).toObject<Transaction>()!!

            val groupRef = ref(groupReference)
            val group = dbTransaction.get(groupRef).toObject<Group>()!!

            group.balance.balances = generateBalances(
                group.balance.balances,
                transaction.lender!!,
                reverseBorrowers(transaction.borrowers),
                -transaction.value
            )
            group.balance.balances =
                generateBalances(group.balance.balances, lender, borrowers, value)
            group.balance.paybacks = generatePaybackList(group.balance.balances)

            transaction.title = title
            transaction.category = category
            transaction.lender = lender
            transaction.description = description
            transaction.value = value
            transaction.borrowers = borrowers

            dbTransaction.set(groupRef, group)
            dbTransaction.set(transactionReference, transaction)
            null
        }.addOnSuccessListener { Log.d(TAG, "transaction done"); callback() }
            .addOnFailureListener { fail -> Log.d(TAG, "$fail") }

    }

    fun deleteTransaction(
        transactionReferencePath: TransactionReference,
        groupReference: GroupReference, callback: () -> Unit
    ) {
        db.runTransaction { dbTransaction ->
            val transactionReference = ref(transactionReferencePath)
            val transaction = dbTransaction.get(transactionReference).toObject<Transaction>()!!

            val groupRef = ref(groupReference)
            val group = dbTransaction.get(groupRef).toObject<Group>()!!

            group.balance.balances = generateBalances(
                group.balance.balances,
                transaction.lender!!,
                reverseBorrowers(transaction.borrowers),
                -transaction.value
            )
            group.balance.paybacks = generatePaybackList(group.balance.balances)

            dbTransaction.set(groupRef, group)
            dbTransaction.delete(transactionReference)
            null
        }.addOnSuccessListener { Log.d(TAG, "transaction done");callback() }
            .addOnFailureListener { fail -> Log.d(TAG, "$fail") }
    }

    private fun reverseBorrowers(b: HashMap<UserReference, Double>): HashMap<UserReference, Double> {
        for (el in b) {
            el.setValue(-el.value)
        }
        return b
    }

    private fun generatePaybackList(b: HashMap<UserReference, Double>): ArrayList<Payback> {
        val p = ArrayList<Payback>()

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
        return p
    }


    fun generateBalances(
        balances: HashMap<UserReference, Double>,
        lender: UserReference,
        borrowers: HashMap<UserReference, Double>,
        value: Double
    ): HashMap<UserReference, Double> {
        if (balances.containsKey(lender)) {
            balances[lender] = value + balances[lender]!!
        } else {
            balances[lender] = value
        }

        for (borrower in borrowers.keys) {
            if (balances.containsKey(borrower)) {
                balances[borrower] = balances[borrower]!! - borrowers[borrower]!!
            } else {
                balances[borrower] = -borrowers[borrower]!!
            }
        }
        return balances
    }

}


class TransactionStorage(groupRefPath: GroupReference) {
    var data: ObservableList<TransactionItem> = ObservableArrayList<TransactionItem>()

    init {
        sub(groupRefPath, {
            data.addAll(0, it)
        }, {
            data.replaceAll { t: TransactionItem? ->
                if (t!!.reference in it.map { it.reference }) {
                    it.find { groupItem -> groupItem.reference == t!!.reference }
                } else {
                    t
                }
            }
        }, { data.removeAll(it) })
    }


    fun sub(
        groupRefPath: GroupReference,
        callback_add: (List<TransactionItem>) -> Unit,
        callback_update: (List<TransactionItem>) -> Unit,
        callback_remove: (List<TransactionItem>) -> Unit
    ) {
        TransactionHandler.transactionsReference(groupRefPath)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->


                var added: MutableList<TransactionItem> = mutableListOf()
                var updated: MutableList<TransactionItem> = mutableListOf()
                var removed: MutableList<TransactionItem> = mutableListOf()

                for (dc in snapshot!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> added.add(
                            TransactionItem(
                                dc.document.reference.path,
                                dc.document.toObject<Transaction>()
                            )
                        )
                        DocumentChange.Type.MODIFIED -> updated.add(
                            TransactionItem(
                                dc.document.reference.path,
                                dc.document.toObject<Transaction>()
                            )
                        )
                        DocumentChange.Type.REMOVED -> removed.add(
                            TransactionItem(
                                dc.document.reference.path,
                                dc.document.toObject<Transaction>()
                            )
                        )

                    }
                }

                callback_add(added)
                callback_update(updated)
                callback_remove(removed)

            }
    }
}

