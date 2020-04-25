package com.example.betterhomefinances.handlers

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject

data class Group(
    val name: String,
    val members: ArrayList<DocumentReference>,
    val balance: Balance
)

object GroupHandler {

    fun group(id: String) = FirestoreHandler.groups.document(id)
    fun group(document_reference: DocumentReference) = document_reference

    fun createGroup() {
        FirestoreHandler.groups
            .add(
                Group(
                    "hello1",
                    arrayListOf(UserHandler.userReference),
                    Balance(HashMap(), ArrayList(), Timestamp.now())
                )
            )
            .addOnSuccessListener { documentReference ->
                UserHandler.userReference.update(
                    "memberOfGroups",
                    FieldValue.arrayUnion(documentReference)
                )
            }
    }

    fun removeGroup(ref: DocumentReference) {
        FirestoreHandler.db.runTransaction { transaction ->
            val group = transaction.get(ref).toObject<Group>()
            for (userRef in group?.members!!) {
                transaction.update(userRef, "memberOfGroups", FieldValue.arrayRemove(ref))
            }
//            transaction.delete(ref)
            //TODO: need to create a Function for deleting subcollections :/
        }
    }
}