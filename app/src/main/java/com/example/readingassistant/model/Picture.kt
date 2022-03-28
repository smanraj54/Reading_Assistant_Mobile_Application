package com.example.readingassistant.model

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class Picture(var name: String = "",
                   var date: String = "",
                   var url: String = "",
                   var text: String = "")
