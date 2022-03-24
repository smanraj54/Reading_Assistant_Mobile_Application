package com.example.readingassistant.persistence

import com.google.firebase.firestore.FirebaseFirestore

class Database {

    fun getConnection(): FirebaseFirestore {
        return FirebaseFirestore.getInstance();
    }

}