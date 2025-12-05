package com.example.copechat

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.copechat.adapter.ProfileLinkAdapter
import com.example.copechat.databinding.FragmentProfileBinding
import com.example.copechat.model.ProfileLink
import android.annotation.SuppressLint

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val linkList = mutableListOf<ProfileLink>()
    private lateinit var linkAdapter: ProfileLinkAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupProfileInfo()
        loadIntro()
        setupAchievements()
        setupButtons()
    }

    private fun setupProfileInfo() {

        Glide.with(this)
            .load(R.drawable.my_profile)
            .circleCrop()
            .into(binding.ivProfileImage)
    }

    private fun setupAchievements() {
        if (linkList.isEmpty()) {
            linkList.add(
                ProfileLink(
                    "https://images.unsplash.com/photo-1532619675605-1ede6c2ed2b0?w=400&h=400&fit=crop",
                    "Deep Learning Research",
                    "A novel approach to predicting protein folding patterns.",
                    "nature.com",
                    "https://nature.com"
                )
            )
            linkList.add(
                ProfileLink(
                    "https://images.unsplash.com/photo-1618401471353-b98afee0b2eb?w=400&h=400&fit=crop",
                    "GitHub - Neural Network Framework",
                    "Open-source deep learning framework with 2.3k stars. Built with PyTorch.",
                    "github.com",
                    "https://github.com"
                )
            )
            linkList.add(
                ProfileLink(
                    "https://images.unsplash.com/photo-1551288049-bebda4e38f71?w=400&h=400&fit=crop",
                    "Data Visualization Dashboard for Genomics",
                    "Interactive web-based dashboard for analyzing large-scale genomic datasets.",
                    "arxiv.org",
                    "https://arxiv.org"
                )
            )
        }

        linkAdapter = ProfileLinkAdapter(linkList) { url ->
            openWebPage(url)
        }
        binding.rvAchievements.layoutManager = LinearLayoutManager(context)
        binding.rvAchievements.adapter = linkAdapter
    }

    private fun setupButtons() {

        // 자기소개 수정 버튼
        binding.btnEditIntro.setOnClickListener {
            showEditIntroDialog()
        }

        // 링크 추가 버튼
        binding.btnAddLink.setOnClickListener {
            showAddLinkDialog()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAddLinkDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_add_link)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val etTitle = dialog.findViewById<EditText>(R.id.et_link_title)
        val etDesc = dialog.findViewById<EditText>(R.id.et_link_desc)
        val etUrl = dialog.findViewById<EditText>(R.id.et_link_url)
        val btnCancel = dialog.findViewById<View>(R.id.btn_cancel)
        val btnAdd = dialog.findViewById<View>(R.id.btn_add)

        btnCancel.setOnClickListener { dialog.dismiss() }

        btnAdd.setOnClickListener {
            val title = etTitle.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val url = etUrl.text.toString().trim()

            if (title.isNotEmpty() && url.isNotEmpty()) {
                // 도메인 추출 (간단한 로직)
                val domain = try {
                    Uri.parse(url).host ?: "web"
                } catch (e: Exception) { "web" }

                // [5] 새 데이터 생성 및 리스트에 추가
                val newLink = ProfileLink(
                    thumbnail = "https://via.placeholder.com/150", // 임시 썸네일 (실제 앱에선 URL 메타데이터 파싱 필요)
                    title = title,
                    description = desc,
                    domain = domain,
                    url = url
                )

                linkList.add(newLink)

                // [6] 어댑터 갱신 (새 항목 반영)
                linkAdapter.notifyItemInserted(linkList.size - 1)

                Toast.makeText(context, "링크가 추가되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "제목과 URL을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun openWebPage(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "링크를 열 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadIntro() {
        val sharedPref = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        val savedIntro = sharedPref.getString("user_intro", null)

        if (savedIntro != null) {
            binding.tvIntro.text = savedIntro
        }
    }

    private fun saveIntro(intro: String) {
        val sharedPref = requireActivity().getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("user_intro", intro)
            apply() // 비동기 저장
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showEditIntroDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_edit_intro)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val etIntro = dialog.findViewById<EditText>(R.id.et_intro)
        val btnCancel = dialog.findViewById<AppCompatButton>(R.id.btn_cancel)
        val btnSave = dialog.findViewById<AppCompatButton>(R.id.btn_save)

        etIntro.setText(binding.tvIntro.text)

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSave.setOnClickListener {
            val newIntro = etIntro.text.toString().trim()
            if (newIntro.isNotEmpty()) {
                binding.tvIntro.text = newIntro

                saveIntro(newIntro)

                Toast.makeText(context, "자기소개가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            } else {
                Toast.makeText(context, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }
}