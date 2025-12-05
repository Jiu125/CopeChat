package com.example.copechat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.copechat.databinding.FragmentCreateMentoringBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import java.text.NumberFormat
import java.util.Locale
import com.example.copechat.model.MentoringSession
import java.text.SimpleDateFormat
import java.util.Calendar


class CreateMentoringBottomSheet : BottomSheetDialogFragment() {
    private lateinit var mainViewModel: MainViewModel
    private var _binding: FragmentCreateMentoringBottomSheetBinding? = null
    private val binding get() = _binding!!

    private var currentStep = 1
    private val maxStep = 3

    // 데이터 저장 변수
    private var category = ""
    private var duration = ""
    private var sessionsPerWeek = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateMentoringBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    // [중요] 화면이 뜰 때 전체 화면으로 확장하는 코드
    override fun onStart() {
        super.onStart()

        val bottomSheetDialog = dialog as? BottomSheetDialog
        val bottomSheet = bottomSheetDialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

        bottomSheet?.let { sheet ->
            // 1. 높이를 전체 화면(MATCH_PARENT)으로 설정
            val params = sheet.layoutParams
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            sheet.layoutParams = params

            // 2. 동작 설정 (완전히 펼쳐진 상태로 시작)
            val behavior = BottomSheetBehavior.from(sheet)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]

        val mentorName = arguments?.getString("mentorName") ?: "멘토"
        val mentorProfileUrl = arguments?.getString("mentorProfileUrl") ?: ""

        setupUI(mentorName, mentorProfileUrl)
//        setupCategories()

        setupListeners()
        updateStepUI()
    }

    private fun setupUI(name: String, profileUrl: String) {
        binding.tvRecipientName.text = name

        Glide.with(this)
            .load(profileUrl)
            .circleCrop()
            .placeholder(R.drawable.bg_circle_placeholder)
            .error(R.drawable.bg_circle_placeholder)
            .into(binding.ivRecipientProfile)
    }

//    private fun setupCategories() {
//        val categories = listOf("프로그래밍", "알고리즘", "웹 개발", "앱 개발", "데이터 과학", "디자인", "마케팅", "비즈니스", "언어", "기타")
//
//        for (cat in categories) {
//            val chip = Chip(context).apply {
//                text = cat
//                isCheckable = true
//                setChipBackgroundColorResource(R.color.white)
//            }
//            binding.chipGroupCategory.addView(chip)
//        }
//    }

    private fun setupListeners() {
        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnNext.setOnClickListener {
            if (validateCurrentStep()) {
                if (currentStep < maxStep) {
                    currentStep++
                    updateStepUI()
                } else {
                    completeMentoringProposal()
                }
            }
        }

        binding.btnPrev.setOnClickListener {
            if (currentStep > 1) {
                currentStep--
                updateStepUI()
            }
        }

        // 업데이트용 리스너
        binding.etWeekSession.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateSummaryCard() }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etPrice.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { updateSummaryCard() }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun validateCurrentStep(): Boolean {
        when (currentStep) {
            1 -> if (binding.etMentoringTitle.text.isNullOrEmpty()) {
                Toast.makeText(context, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return false
            }
            2 -> if (binding.etPrice.text.isNullOrEmpty()) {
                Toast.makeText(context, "가격을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return false
            }
        }
        return true
    }

    private fun updateStepUI() {
        binding.tvStep.text = "단계 $currentStep/$maxStep"
        updateProgressBar()

        if (currentStep == 1) {
            binding.btnPrev.visibility = View.GONE
            binding.spaceButton.visibility = View.GONE
            binding.btnNext.text = "다음"
        } else {
            binding.btnPrev.visibility = View.VISIBLE
            binding.spaceButton.visibility = View.VISIBLE
            binding.btnNext.text = if (currentStep == maxStep) "멘토링 제안하기" else "다음"
        }

        binding.step1Layout.visibility = if (currentStep == 1) View.VISIBLE else View.GONE
        binding.step2Layout.visibility = if (currentStep == 2) View.VISIBLE else View.GONE
        binding.step3Layout.visibility = if (currentStep == 3) View.VISIBLE else View.GONE

        if (currentStep == 3) updateSummaryCard()
    }

    private fun updateSummaryCard() {
        val title = binding.etMentoringTitle.text.toString()
        val priceStr = binding.etPrice.text.toString()
        val weekSessionStr = binding.etWeekSession.text.toString()

        val categoryChipId = binding.chipGroupCategory.checkedChipId
        if (categoryChipId != View.NO_ID) {
            category = binding.chipGroupCategory.findViewById<Chip>(categoryChipId).text.toString()
        }

        val durationChipId = binding.chipGroupDuration.checkedChipId
        if (durationChipId != View.NO_ID) {
            duration = binding.chipGroupDuration.findViewById<Chip>(durationChipId).text.toString()
        }

        binding.tvSummaryTitle.text = if (title.isNotEmpty()) title else "-"
//        binding.tvSummaryCategory.text = if (category.isNotEmpty()) category else "-"
        binding.tvSummaryDuration.text = if (duration.isNotEmpty()) duration else "-"

        val price = priceStr.replace(",", "").toLongOrNull() ?: 0
        val Session = weekSessionStr.toIntOrNull() ?: 0

        val formatter = NumberFormat.getNumberInstance(Locale.KOREA)
        binding.tvSummaryPrice.text = "${formatter.format(price)}원"

        if (Session > 0) {
            val totalSession = price / Session
            binding.tvSummaryPerSession.text = "${formatter.format(totalSession)}원"
        } else {
            binding.tvSummaryPerSession.text = "0원"
        }
    }

    private fun updateProgressBar() {
        val activeDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_progress_active)
        val inactiveDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.bg_progress_inactive)

        binding.progressStep1.background = if (currentStep >= 1) activeDrawable else inactiveDrawable
        binding.progressStep2.background = if (currentStep >= 2) activeDrawable else inactiveDrawable
        binding.progressStep3.background = if (currentStep >= 3) activeDrawable else inactiveDrawable
    }

    private fun completeMentoringProposal() {
        val title = binding.etMentoringTitle.text.toString()
        val mentorName = binding.tvRecipientName.text.toString()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 3)
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val futureTime = calendar.time

        val dateFormat = SimpleDateFormat("yyyy.MM.dd(E)", Locale.KOREA)
        val timeFormat = SimpleDateFormat("a h:mm", Locale.KOREA)

        val newSession = MentoringSession(
            id = "room_${System.currentTimeMillis()}",
            mentorName = mentorName,
            mentorProfileUrl = arguments?.getString("mentorProfileUrl") ?: "",
            sessionTitle = title,
            date = dateFormat.format(futureTime),
            time = timeFormat.format(futureTime),
            dDay = "D-1",
            isEnterEnabled = true
        )

        mainViewModel.addSession(newSession)

        Toast.makeText(context, "멘토링이 생성되었습니다!", Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme
}