package com.example.copechat.model

import android.R
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class ChatRoomModel(
    val id: String = "",
    val name: String = "",
    val profileImageUrl: String = "",
    val lastMessage: String = "",
    val timestamp: Date? = null,
    val unreadCount: Int = 0
)

data class ChatMessageModel(
    val id: String = "",
    val message: String = "",
    val senderId: String = "",
    val senderProfileUrl: String? = null,
    @ServerTimestamp
    val timestamp: Date? = null,
)