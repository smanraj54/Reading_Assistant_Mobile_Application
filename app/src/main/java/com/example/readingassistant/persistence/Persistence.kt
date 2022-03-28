package com.example.readingassistant.persistence

import android.annotation.SuppressLint
import android.util.Log
import com.example.readingassistant.MainActivity
import com.example.readingassistant.model.Category
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

@SuppressLint("RestrictedApi")
object Persistence {
    var categories = ArrayList<Category>()

    lateinit var mainActivity : MainActivity
}