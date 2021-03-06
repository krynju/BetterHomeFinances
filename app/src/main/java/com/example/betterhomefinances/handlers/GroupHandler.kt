package com.example.betterhomefinances.handlers

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.example.betterhomefinances.handlers.FirestoreHandler.db
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
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
        val registration =
            UserHandler.currentUserDocumentReference.addSnapshotListener { snapshot, e ->
                val ud = snapshot?.toObject<UserDetails>()!!
                val currentIds = data.map { groupItem -> groupItem.reference.split("/")[1] }.toSet()
                val fetchedIds = ud.memberOfGroups.map { id -> id.split("/")[1] }.toSet()
                if (currentIds.union(fetchedIds).size > currentIds.size) {
                    getGroupsRefPair(fetchedIds.subtract(currentIds).toList(),
                        { data.addAll(0, it) },
                        {
                            data.replaceAll { t: GroupItem? ->
                                if (t!!.reference in it.map { it.reference }) {
                                    it.find { groupItem -> groupItem.reference == t!!.reference }
                                } else {
                                    t
                                }
                            }
                        },
                        {})
                } else if (fetchedIds.size < currentIds.size) {
                    val el = currentIds.subtract(fetchedIds)
                    val toRemove = data.filter { it.reference.split("/")[1] in el }
                    data.removeAll(toRemove)
                }
            }
        FirestoreHandler.registrations.add(registration)
    }

    fun transactions(groupRefPath: GroupReference): TransactionStorage {
        return TransactionHandler.getInstance(groupRefPath)
    }

    fun group(id: GroupReference) = FirestoreHandler.groups.document(id)

    fun group(document_reference: DocumentReference) = document_reference
    fun groupFromRef(groupRefPath: GroupReference) = FirestoreHandler.db.document(groupRefPath)

    fun createGroup(name: String, description: String, callback: (String) -> Unit) {
        FirestoreHandler.groups
            .add(
                Group(
                    name,
                    arrayListOf(UserHandler.currentUserDocumentReference.path),
                    Balance(HashMap(), ArrayList(), Timestamp.now()),
                    description
                )
            )
            .addOnSuccessListener { documentReference ->
                UserHandler.currentUserDocumentReference.update(
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
                    FieldValue.arrayRemove(ref.path)
                )
            }
//            transaction.delete(ref)
            //TODO: need to create a Function for deleting subcollections :/
        }
    }

    fun joinGroup(groupCode: String, callback_success: () -> Unit, callback_fail: () -> Unit) {
        val groupRef = "groups/" + groupCode

        db.runTransaction { dbTransaction ->
            val group = dbTransaction.get(db.document(groupRef))
            if (group.exists()) {

                dbTransaction.update(
                    group.reference,
                    "members",
                    FieldValue.arrayUnion(UserHandler.currentUserReference)
                )
                dbTransaction.update(
                    UserHandler.currentUserDocumentReference,
                    "memberOfGroups",
                    FieldValue.arrayUnion(groupRef)
                )
            }

        }.addOnSuccessListener { callback_success() }.addOnFailureListener { callback_fail() }
    }

    private fun getGroupsRefPair(
        groupIds: List<String>,
        callback_add: (List<GroupItem>) -> Unit,
        callback_update: (List<GroupItem>) -> Unit,
        callback_remove: (List<GroupItem>) -> Unit
    ) {
        UserHandler.getUserDetails(UserHandler.currentUserDocumentReference, {
            val registration = FirestoreHandler.groups.whereIn(FieldPath.documentId(), groupIds)
                .addSnapshotListener { result, e ->

                    val added: MutableList<GroupItem> = mutableListOf()
                    val updated: MutableList<GroupItem> = mutableListOf()
                    val removed: MutableList<GroupItem> = mutableListOf()
                    if (result != null) {
                        for (dc in result.documentChanges) {
                            when (dc.type) {
                                DocumentChange.Type.ADDED -> added.add(
                                    GroupItem(
                                        dc.document.reference.path,
                                        dc.document.toObject<Group>()
                                    )
                                )
                                DocumentChange.Type.MODIFIED -> updated.add(
                                    GroupItem(
                                        dc.document.reference.path,
                                        dc.document.toObject<Group>()
                                    )
                                )
                                DocumentChange.Type.REMOVED -> removed.add(
                                    GroupItem(
                                        dc.document.reference.path,
                                        dc.document.toObject<Group>()
                                    )
                                )
                            }
                        }
                    }
                    callback_add(added)
                    callback_update(updated)
                    callback_remove(removed)
                }
            FirestoreHandler.registrations.add(registration)
        }, {})


    }

    fun leaveGroup(reference: GroupReference) {
        db.runTransaction { dbTransaction ->
            val group = dbTransaction.get(db.document(reference))
            if (group.exists()) {
                dbTransaction.update(
                    group.reference,
                    "members",
                    FieldValue.arrayRemove(UserHandler.currentUserReference)
                )
                dbTransaction.update(
                    UserHandler.currentUserDocumentReference,
                    "memberOfGroups",
                    FieldValue.arrayRemove(reference)
                )
            }
        }
    }


}