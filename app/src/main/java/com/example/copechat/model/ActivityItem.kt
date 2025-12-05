package com.example.copechat.model

enum class ActivityType {
    MENTORING, TRANSLATION
}

enum class ActivityStatus {
    UPCOMING, COMPLETED
}

data class ActivityItem(
    val title: String,
    val subtitle: String,
    val time: String,
    val type: ActivityType,
    val status: ActivityStatus
)