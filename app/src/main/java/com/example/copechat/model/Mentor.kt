package com.example.copechat.model

import com.google.firebase.firestore.PropertyName

data class Mentor(
    val name: String = "",
    val school: String = "",
    val major: String = "",
    val rating: String = "0.0",
    val reviewCount: String = "(0)",
    val imageUrl: String = "",

    @PropertyName("verified")
    val isVerified: Boolean = false,

    val tags: List<String> = emptyList(),
    val introduction: String = ""
)