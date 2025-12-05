package com.example.copechat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.copechat.adapter.MentorListAdapter
import com.example.copechat.databinding.FragmentFindMentorBinding
import com.example.copechat.model.Mentor
import com.google.firebase.firestore.FirebaseFirestore

class FindMentorFragment : Fragment() {

    private var _binding: FragmentFindMentorBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private enum class SortType {
        DEFAULT, RATING, REVIEW
    }
    private var currentSortType = SortType.DEFAULT

    // 전체 데이터 보관용
    private var allMentors = mutableListOf<Mentor>()

    private lateinit var adapter: MentorListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFindMentorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupSearch()
        setupFilters()

        loadMentorsFromFirebase()
    }

    private fun loadMentorsFromFirebase() {
        db.collection("mentors")
            .get()
            .addOnSuccessListener { result ->
                allMentors.clear()
                for (document in result) {
                    val mentor = document.toObject(Mentor::class.java)
                    allMentors.add(mentor)
                }

                filterAndSortList()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(context, "멘토 목록을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                Log.e("FindMentorFragment", "Error getting documents: ", exception)
            }
    }

    private fun setupRecyclerView() {
        adapter = MentorListAdapter(
            items = allMentors,
            onRequestClick = { mentor ->
                (activity as? MainActivity)?.moveToChatRoom("room_test_1")
            },
            onItemClick = { mentor ->
                (activity as? MainActivity)?.showMentorProfile(mentor)
            },
            onMessageClick = { mentor ->
                (activity as? MainActivity)?.enterChatWithMentor(mentor.name, mentor.imageUrl)
            }
        )
        binding.rvMentorList.adapter = adapter
        binding.rvMentorList.layoutManager = LinearLayoutManager(context)
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterAndSortList()
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setupFilters() {
        binding.chipVerified.setOnCheckedChangeListener { _, _ ->
            filterAndSortList()
        }

        binding.chipRating.setOnClickListener {
            currentSortType = SortType.RATING
            filterAndSortList()
        }

        binding.chipReview.setOnClickListener {
            currentSortType = SortType.REVIEW
            filterAndSortList()
        }
    }

    private fun filterAndSortList() {
        val query = binding.etSearch.text.toString().trim()
        val isVerifiedOnly = binding.chipVerified.isChecked

        val filtered = allMentors.filter { mentor ->
            val matchesSearch = mentor.name.contains(query, ignoreCase = true) ||
                    mentor.major.contains(query, ignoreCase = true) ||
                    mentor.school.contains(query, ignoreCase = true)

            val matchesFilter = if (isVerifiedOnly) mentor.isVerified else true

            matchesSearch && matchesFilter
        }

        // 2. 정렬 (평점, 리뷰수 숫자 변환 후 내림차순)
        val sortedList = when (currentSortType) {
            SortType.RATING -> filtered.sortedByDescending {
                it.rating.toDoubleOrNull() ?: 0.0
            }
            SortType.REVIEW -> filtered.sortedByDescending {
                // "(28)" 같은 문자열에서 숫자만 추출
                it.reviewCount.filter { char -> char.isDigit() }.toIntOrNull() ?: 0
            }
            SortType.DEFAULT -> filtered
        }

        // 3. 어댑터 갱신
        adapter.updateList(sortedList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}