package com.example.betterhomefinances.dummy

import com.example.betterhomefinances.handlers.FirestoreHandler
import com.example.betterhomefinances.handlers.Group
import com.example.betterhomefinances.handlers.GroupReference
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object GroupContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<GroupItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, GroupItem> = HashMap()

    var groups: MutableList<Pair<GroupReference, Group>> = ArrayList()

    private val COUNT = 25

    init {
        // Add some sample items.


        FirestoreHandler.groups.get().addOnSuccessListener { result ->

            for (docRef in result.documents) {
//                docRef.toObject<Group>()?.let { groups.add(Pair(it.toString(), it)) }

                groups.add(Pair(docRef.reference.path, docRef.toObject<Group>()!!))
            }


            for (i in 1..COUNT) {
                addItem(createDummyItem(i, groups[0].second.name.toString()))
            }
        }

    }

    fun getContent(callback: (List<Pair<GroupReference, Group>>) -> Unit) {
        FirestoreHandler.groups.get()
            .addOnSuccessListener { result ->
                callback(result.map {
                    Pair(
                        it.reference.path,
                        it.toObject<Group>()
                    )
                })
            }
    }

    private fun addItem(item: GroupItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int, text: String): GroupItem {
        return GroupItem(position.toString(), "Item " + position + text, makeDetails(position))
    }

    private fun makeDetails(position: Int): String {
        val builder = StringBuilder()
        builder.append("Details about Item: ").append(position)
        for (i in 0..position - 1) {
            builder.append("\nMore details information here.")
        }
        return builder.toString()
    }

    /**
     * A dummy item representing a piece of content.
     */
    data class GroupItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
