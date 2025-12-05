package com.example.copechat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.copechat.adapter.ChatRoomAdapter
import com.example.copechat.databinding.FragmentChatRoomBinding
import com.example.copechat.model.ChatMessageModel
import com.example.copechat.model.Mentor
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import android.widget.PopupMenu

class ChatRoomFragment : Fragment() {

    private var _binding: FragmentChatRoomBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    private var roomName: String = "알 수 없는 채팅방"
    private var roomId: String = ""
    private val myUid: String = "user_1"
    private var currentMentorName: String = ""
    private var currentMentorImage: String = ""

    private lateinit var adapter: ChatRoomAdapter
    private val messageList = mutableListOf<ChatMessageModel>()

    // [2] 리스너를 담아둘 변수 선언
    private var messageListener: ListenerRegistration? = null
    private var roomInfoListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatRoomBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        roomId = arguments?.getString("roomId") ?: ""

        setupRoomInfo()

        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.btnMenu.setOnClickListener { view ->
            showPopupMenu(view)
        }

        binding.btnSend.setOnClickListener {
            sendMessage()
        }

        setupRecyclerView()
        observeMessages()
    }

    override fun onResume() {
        super.onResume()
        resetMyUnreadCount()
    }

    private fun resetMyUnreadCount() {
        if (roomId.isNotEmpty()) {
            db.collection("chat_rooms").document(roomId)
                .update("unreadCount_$myUid", 0)
        }
    }

    private fun setupRecyclerView() {
        adapter = ChatRoomAdapter(messageList, myUid)

        binding.rvChatMessages.layoutManager = LinearLayoutManager(requireContext()).apply {
            stackFromEnd = true
        }
        binding.rvChatMessages.adapter = adapter
    }

    private fun sendMessage() {
        val text = binding.etMessage.text.toString().trim()
        if (text.isEmpty()) return

        val roomRef = db.collection("chat_rooms").document(roomId)
        val messagesRef = roomRef.collection("messages").document()

        val newMessage = ChatMessageModel(
            message = text,
            senderId = myUid,
            timestamp = null
        )

        db.runTransaction { transaction ->
            transaction.set(messagesRef, newMessage)
            transaction.update(roomRef, "lastMessage", text)
            transaction.update(roomRef, "timestamp", FieldValue.serverTimestamp())
        }.addOnSuccessListener {
            // binding이 null일 수도 있으므로 안전하게 호출 (선택사항)
            _binding?.etMessage?.text?.clear()
        }.addOnFailureListener { e ->
            Toast.makeText(context, "전송 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeMessages() {
        // [3] 리스너를 변수에 저장 (messageListener = ...)
        messageListener = db.collection("chat_rooms").document(roomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, e ->
                // 화면이 죽었거나 에러가 있으면 중단
                if (_binding == null || e != null) {
                    Log.w("ChatRoomFragment", "Listen failed or view destroyed.", e)
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    messageList.clear()
                    for (doc in snapshots) {
                        val message = doc.toObject(ChatMessageModel::class.java)
                        val messageWithId = message.copy(id = doc.id)
                        messageList.add(messageWithId)
                    }

                    // [핵심] binding이 유효할 때만 UI 업데이트
                    if (_binding != null) {
                        adapter.notifyDataSetChanged()
                        if (messageList.isNotEmpty()) {
                            binding.rvChatMessages.scrollToPosition(messageList.size - 1)
                        }
                    }
                }
            }
    }

    // 팝업 메뉴 표시 함수
    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(R.menu.menu_chat_room, popup.menu)

        try {
            val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
            fieldMPopup.isAccessible = true
            val mPopup = fieldMPopup.get(popup)
            mPopup.javaClass
                .getDeclaredMethod("setForceShowIcon", Boolean::class.javaPrimitiveType)
                .invoke(mPopup, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_create_mentoring -> {
                    showCreateMentoringSheet()
                    true
                }
                R.id.action_view_profile -> {
                    val mentor = Mentor(
                        name = currentMentorName,
                        imageUrl = currentMentorImage,
                        school = "서울대학교",
                        major = "컴퓨터공학",
                        rating = "4.9",
                        reviewCount = "127",
                        isVerified = true,
                        tags = emptyList(),
                        introduction = "안녕하세요! 멘토링을 통해 성장을 돕겠습니다."
                    )

                    // MainActivity의 프로필 이동 함수 호출
                    (activity as? MainActivity)?.showMentorProfile(mentor)
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun setupRoomInfo() {
        if (roomId.isEmpty()) {
            binding.tvRoomName.text = "오류: 방 ID 없음"
            return
        }

//        roomInfoListener = db.collection("chat_rooms").document(roomId)
//            .addSnapshotListener { snapshot, e ->
//                if (e != null) return@addSnapshotListener
//
//                if (snapshot != null && snapshot.exists()) {
//                    // DB의 "name" 필드를 가져와서 제목에 설정
//                    val name = snapshot.getString("name") ?: "알 수 없는 사용자"
//                    binding.tvRoomName.text = name
//                }
//            }
        val roomListener = db.collection("chat_rooms").document(roomId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener

                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name") ?: "알 수 없는 사용자"
                    val profileUrl = snapshot.getString("profileImageUrl") ?: ""

                    binding.tvRoomName.text = name

                    // [핵심] 메뉴에서 사용할 수 있도록 변수에 저장
                    currentMentorName = name
                    currentMentorImage = profileUrl
                }
            }
    }

    private fun showCreateMentoringSheet() {
        val bottomSheet = CreateMentoringBottomSheet().apply {
            arguments = Bundle().apply {
                putString("mentorName", currentMentorName)
                putString("mentorProfileUrl", currentMentorImage)
            }
        }
        bottomSheet.show(parentFragmentManager, "CreateMentoringBottomSheet")
    }

    override fun onDestroyView() {
        super.onDestroyView()

        messageListener?.remove()
        roomInfoListener?.remove()

        messageListener = null
        roomInfoListener = null
        _binding = null
    }
}