package com.example.betterhomefinances.dummy

import com.example.betterhomefinances.handlers.FirestoreHandler
import com.example.betterhomefinances.handlers.Group
import com.google.firebase.firestore.ktx.toObject
import java.util.*
import kotlin.collections.ArrayList

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 *
 * TODO: Replace all uses of this class before publishing your app.
 */
object DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    val ITEMS: MutableList<DummyItem> = ArrayList()

    /**
     * A map of sample (dummy) items, by ID.
     */
    val ITEM_MAP: MutableMap<String, DummyItem> = HashMap()

    var groups: MutableList<Group> = ArrayList()

    private val COUNT = 25

    init {
        // Add some sample items.


        FirestoreHandler.groups.get().addOnSuccessListener { result ->

            for (docRef in result.documents) {
                docRef.toObject<Group>()?.let { groups.add(it) }
            }


            for (i in 1..COUNT) {
                addItem(createDummyItem(i, groups[0].name.toString()))
            }
        }

    }

    fun getContent(callback: (List<Group>) -> Unit) {
        FirestoreHandler.groups.get()
            .addOnSuccessListener { result -> callback(result.map { it.toObject<Group>() }) }
    }

    private fun addItem(item: DummyItem) {
        ITEMS.add(item)
        ITEM_MAP.put(item.id, item)
    }

    private fun createDummyItem(position: Int, text: String): DummyItem {
        return DummyItem(position.toString(), "Item " + position + text, makeDetails(position))
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
    data class DummyItem(val id: String, val content: String, val details: String) {
        override fun toString(): String = content
    }
}
