package com.example.copechat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.copechat.model.MentoringSession
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainViewModel : ViewModel() {

    // 멘토링 세션 목록을 담는 LiveData
    private val _mentoringSessions = MutableLiveData<MutableList<MentoringSession>>(mutableListOf())
    val mentoringSessions: LiveData<MutableList<MentoringSession>> get() = _mentoringSessions

    val calendar = Calendar.getInstance()

    val futureTime = calendar.time

    val dateFormat = SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREA)
    val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

    val dateStr = dateFormat.format(futureTime)
    val timeStr = timeFormat.format(futureTime)

    // 초기 더미 데이터 추가 (기존에 있던 김수진 멘토 데이터)
    init {
        calendar.add(Calendar.MINUTE, 3)

        addSession(
            MentoringSession(

                id = "room_user_1_김수진",
                mentorName = "김수진",
                mentorProfileUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop",
                sessionTitle = "알고리즘 스터디 멘토링",
                date = dateStr,
                time = timeStr,
                dDay = "D-0",
                isEnterEnabled = true
            )
        )
    }

    // 세션 추가 함수
    fun addSession(session: MentoringSession) {
        val currentList = _mentoringSessions.value ?: mutableListOf()
        currentList.add(0, session) // 최신 항목을 맨 위에 추가
        _mentoringSessions.value = currentList // 변경 사항 알림
    }
}