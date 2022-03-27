package com.example.readingassistant.model

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Category(var title: String = "",
                    var desc: String = "",
                    var pictrues: List<Picture> = ArrayList()
)
