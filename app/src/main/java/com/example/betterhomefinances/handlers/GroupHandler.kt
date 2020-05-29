package com.example.betterhomefinances.handlers

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject

typealias GroupReference = String

data class Group(
    val name: String? = null,
    val members: ArrayList<UserReference> = arrayListOf(),
    val balance: Balance = Balance(),
    val description: String = ""
)

data class GroupItem(
    val reference: GroupReference,
    val group: Group
)

object GroupHandler {

    var data: ObservableList<GroupItem> = ObservableArrayList<GroupItem>()

    init {
        getGroupsRefPair {
            data.addAll(it)
        }
    }

    fun transactions(groupRefPath: GroupReference): TransactionStorage {
        return TransactionHandler.getInstance(groupRefPath)
    }

    fun group(id: GroupReference) = FirestoreHandler.groups.document(id)

    fun group(document_reference: DocumentReference) = document_reference

    fun createGroup(name: String, description: String, callback: (String) -> Unit) {
        FirestoreHandler.groups
            .add(
                Group(
                    name,
                    arrayListOf(UserHandler.currentUserReference.path),
                    Balance(HashMap(), ArrayList(), Timestamp.now()),
                    description
                )
            )
            .addOnSuccessListener { documentReference ->
                UserHandler.currentUserReference.update(
                    "memberOfGroups",
                    FieldValue.arrayUnion(documentReference.path)
                )
                callback(documentReference.path)
            }
    }

    fun removeGroup(ref: DocumentReference) {
        db.runTransaction { transaction ->
            val group = transaction.get(ref).toObject<Group>()
            for (userRef in group?.members!!) {
                transaction.update(
                    db.document(userRef),
                    "memberOfGroups",
                    FieldValue.arrayRemove(ref)
                )
            }
//            transaction.delete(ref)
            //TODO: need to create a Function for deleting subcollections :/
        }
    }

    private fun getGroupsRefPair(callback: (List<GroupItem>) -> Unit) {
        FirestoreHandler.groups.get()
            .addOnSuccessListener { result ->
                callback(result.map {
                    GroupItem(
                        it.reference.path,
                        it.toObject<Group>()
                    )
                })
            }
    }
}