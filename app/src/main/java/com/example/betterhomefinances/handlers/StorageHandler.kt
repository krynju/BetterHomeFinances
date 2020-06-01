package com.example.betterhomefinances.handlers

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


object StorageHandler {
    var mStorageRef: StorageReference? = null

    init {
        mStorageRef = FirebaseStorage.getInstance().reference;

    }


}