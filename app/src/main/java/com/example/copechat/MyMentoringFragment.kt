package com.example.copechat

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.copechat.adapter.ActivityAdapter
import com.example.copechat.adapter.MentoringSessionAdapter
import com.example.copechat.databinding.FragmentMyMentoringBinding
import com.example.copechat.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

class MyMentoringFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sessionAdapter: MentoringSessionAdapter
    private var _binding: FragmentMyMentoringBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMyMentoringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        setupRecyclerView()
        setupPastActivities()

        mainViewModel.mentoringSessions.observe(viewLifecycleOwner) { sessions ->
            sessionAdapter.updateData(sessions)
        }
    }

    private fun setupRecyclerView() {
        sessionAdapter = MentoringSessionAdapter(
            items = emptyList(), // 처음엔 빈 리스트
            onEnterClick = {
                try {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            onMessageClick = { session ->
                (activity as? MainActivity)?.enterChatWithMentor(
                    session.mentorName,
                    session.mentorProfileUrl
                )
            },
            onProfileClick = { session ->
                val tempMentor = com.example.copechat.model.Mentor(
                    name = session.mentorName,
                    school = "서울대학교",
                    major = "컴퓨터공학",
                    rating = "4.9",
                    reviewCount = "127",
                    imageUrl = session.mentorProfileUrl,
                    isVerified = true,
                    tags = emptyList(),
                    introduction = "알고리즘 스터디 멘토입니다"
                )
                (activity as? MainActivity)?.showMentorProfile(tempMentor)
            }
        )
        binding.rvUpcomingSessions.layoutManager = LinearLayoutManager(context)
        binding.rvUpcomingSessions.adapter = sessionAdapter
    }

    private fun setupUpcomingSessions() {
        val calendar = Calendar.getInstance()


        calendar.add(Calendar.MINUTE, 3)
        val futureTime = calendar.time

        val dateFormat = SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREA)
        val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

        val dateStr = dateFormat.format(futureTime)
        val timeStr = timeFormat.format(futureTime)

        // 더미 데이터
        val sessions = listOf(
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

        val adapter = MentoringSessionAdapter(sessions,
            onEnterClick = {
                try {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            onMessageClick = { session ->
                (activity as? MainActivity)?.enterChatWithMentor(
                    session.mentorName,
                    session.mentorProfileUrl
                )
            },
            onProfileClick = { session ->
                val tempMentor = com.example.copechat.model.Mentor(
                    name = session.mentorName,
                    school = "서울대학교",
                    major = "컴퓨터공학",
                    rating = "4.9",
                    reviewCount = "127",
                    imageUrl = session.mentorProfileUrl,
                    isVerified = true,
                    tags = emptyList(),
                    introduction = "안녕하세요! 멘토링을 통해 성장을 돕겠습니다."
                )

                // 2. 멘토 프로필 화면으로 이동
                (activity as? MainActivity)?.showMentorProfile(tempMentor)
            }
        )
        binding.rvUpcomingSessions.adapter = adapter
    }

    private fun setupPastActivities() {
        val activities = listOf(
//            ActivityItem("자기소개서 첨삭 완료", "김수진 멘토", "11. 20", ActivityType.MENTORING, ActivityStatus.COMPLETED),
            ActivityItem("프로젝트 피드백 세션", "박민수 멘토", "11. 24", ActivityType.MENTORING, ActivityStatus.COMPLETED),
            ActivityItem("백엔드 시스템 설계 리뷰", "박서연 멘토", "11. 18", ActivityType.TRANSLATION, ActivityStatus.COMPLETED)
        )
        binding.rvPastActivities.adapter = ActivityAdapter(activities)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}