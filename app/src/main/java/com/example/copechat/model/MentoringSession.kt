package com.example.copechat.model

data class MentoringSession(
    val id: String,
    val mentorName: String,
    val mentorProfileUrl: String,
    val sessionTitle: String,
    val date: String,
    val time: String,
    val dDay: String, // 예: "D-1", "Today"
    val isEnterEnabled: Boolean // 입장 가능 여부 (시간 다가왔을 때)
)