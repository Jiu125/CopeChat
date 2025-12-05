package com.example.copechat

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.copechat.adapter.ProfileLinkAdapter
import com.example.copechat.databinding.FragmentMentorProfileBinding
import com.example.copechat.model.Mentor
import com.example.copechat.model.ProfileLink
import com.google.firebase.firestore.FirebaseFirestore

class MentorProfileFragment : Fragment() {

    private var _binding: FragmentMentorProfileBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    // 데이터를 저장할 변수들 (기본값 설정)
    private var name: String = ""
    private var school: String = ""
    private var major: String = ""
    private var imageUrl: String = ""
    private var introduction: String = ""
    private var rating: String = ""
    private var reviewCount: String = ""
    private var isVerified: Boolean = false

    // 1. 프래그먼트 생성 시 데이터 받기
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString("name", "")
            school = it.getString("school", "")
            major = it.getString("major", "")
            imageUrl = it.getString("imageUrl", "")
            introduction = it.getString("introduction", "")
            rating = it.getString("rating", "")
            reviewCount = it.getString("reviewCount", "")
            isVerified = it.getBoolean("isVerified", true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMentorProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()           // 데이터 표시

        if (name.isNotEmpty()) {
            loadMentorInfoFromFirebase(name)
        }

        setupAchievements() // 링크 목록 표시
        setupListeners()    // 버튼 클릭 설정
    }

    private fun loadMentorInfoFromFirebase(mentorName: String) {
        // 'mentors' 컬렉션에서 'name' 필드가 mentorName과 일치하는 문서 찾기
        db.collection("mentors")
            .whereEqualTo("name", mentorName)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 첫 번째 검색 결과 사용
                    val document = documents.documents[0]
                    val mentor = document.toObject(Mentor::class.java)

                    if (mentor != null) {
                        // 가져온 최신 정보로 변수 업데이트
                        school = mentor.school
                        major = mentor.major
                        introduction = mentor.introduction
                        rating = mentor.rating
                        reviewCount = mentor.reviewCount
                        isVerified = mentor.isVerified
                        imageUrl = mentor.imageUrl

                        // UI 갱신
                        setupUI()
                    }
                } else {
                    Log.d("Firestore", "해당 이름의 멘토 정보를 찾을 수 없습니다: $mentorName")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", "데이터 가져오기 실패: ", exception)
            }
    }

    private fun setupUI() {
        binding.tvName.text = name

        // 학교 정보가 없으면 숨기거나 기본값 표시
        if (school.isNotEmpty() && major.isNotEmpty()) {
            binding.tvAffiliation.text = "$school • $major"
        } else {
            binding.tvAffiliation.text = "소속 정보 불러오는 중..."
        }

        binding.tvIntroduction.text = if (introduction.isNotEmpty()) introduction else "소개글이 없습니다."
//        binding.tvRating.text = rating
//        binding.tvReviewCount.text = reviewCount // "(127)" 형태

        if (isVerified) {
            binding.ivVerifiedBadge.visibility = View.VISIBLE
            binding.tvVerifiedLabel.visibility = View.VISIBLE
        } else {
            binding.ivVerifiedBadge.visibility = View.GONE
            binding.tvVerifiedLabel.visibility = View.GONE
        }

        // 이미지 로드 (Glide)
        if (imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .placeholder(R.drawable.bg_circle_placeholder)
                .into(binding.ivProfileImage)
        }
    }

    private fun setupAchievements() {
        // (임시) 더미 링크 데이터
        val links = listOf(
            ProfileLink("https://github.com", "GitHub Projects", "오픈소스 기여 내역", "github.com", "https://github.com"),
            ProfileLink("https://medium.com", "Tech Blog", "기술 블로그 포스팅", "medium.com", "https://medium.com")
        )

        binding.rvAchievements.layoutManager = LinearLayoutManager(context)
        binding.rvAchievements.adapter = ProfileLinkAdapter(links) { url ->
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {
                Toast.makeText(context, "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }

        binding.btnMessage.setOnClickListener {
            (activity as? MainActivity)?.enterChatWithMentor(name, imageUrl)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}