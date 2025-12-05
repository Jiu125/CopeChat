package com.example.copechat.model

data class ProfileLink(
    val thumbnail: String,   // 썸네일 이미지 URL
    val title: String,       // 링크 제목
    val description: String, // 설명 (두 줄 요약)
    val domain: String,      // 도메인 (예: github.com)
    val url: String          // 실제 이동할 링크
)