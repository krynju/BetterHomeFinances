package com.example.betterhomefinances.handlers

import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.toObject

data class Group(
    val name: String? = null,
    val members: ArrayList<String> = arrayListOf(),
    val balance: Balance = Balance()
)

object GroupHandler {

    fun group(id: String) = FirestoreHandler.groups.document(id)
    fun group(document_reference: DocumentReference) = document_reference

    fun createGroup() {
        FirestoreHandler.groups
            .add(
                Group(
                    "TEST_GROUP",
                    arrayListOf(UserHandler.userReference.path),
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
}