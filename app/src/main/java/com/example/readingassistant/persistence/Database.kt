package com.example.readingassistant.persistence

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Database {

    public fun getConnection(): FirebaseFirestore {
        return FirebaseFirestore.getInstance();
    }

}