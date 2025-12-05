package com.example.copechat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.copechat.adapter.ChatListAdapter
import com.example.copechat.databinding.FragmentChatListBinding
import com.example.copechat.model.ChatRoomModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration // [1] 이 import가 필요합니다
import com.google.firebase.firestore.Query

class ChatListFragment : Fragment() {

    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()
    private val myUid = "user_1"

    // [2] 리스너를 저장할 변수 선언
    private var chatListListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupChatList()
    }

    private fun setupChatList() {
        // [3] 리스너 결과를 변수에 저장 (snapshotListener = ... 형태로 변경)
        chatListListener = db.collection("chat_rooms")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                // _binding이 null이면(화면이 죽었으면) UI 업데이트 중단 (안전장치)
                if (_binding == null || e != null) return@addSnapshotListener

                val chatRooms = mutableListOf<ChatRoomModel>()

                for (doc in snapshots!!) {
                    val name = doc.getString("name") ?: "알 수 없음"
                    val lastMessage = doc.getString("lastMessage") ?: ""

                    // (이전에 수정한 날짜 처리 부분)
                    val timestamp = doc.getDate("timestamp")
                    val profileUrl = doc.getString("profileImageUrl") ?: ""
                    val myUnreadCount = doc.getLong("unreadCount_$myUid")?.toInt() ?: 0

                    chatRooms.add(
                        ChatRoomModel(
                            id = doc.id,
                            name = name,
                            profileImageUrl = profileUrl,
                            lastMessage = lastMessage,
                            timestamp = timestamp,
                            unreadCount = myUnreadCount
                        )
                    )
                }
                updateAdapter(chatRooms)
            }
    }

    private fun updateAdapter(chatRooms: List<ChatRoomModel>) {
        // 바인딩이 유효할 때만 UI 접근
        if (_binding != null) {
            val adapter = ChatListAdapter(chatRooms) { selectedRoom ->
                (activity as? MainActivity)?.moveToChatRoom(selectedRoom.id)
            }
            binding.rvChatList.layoutManager = LinearLayoutManager(requireContext())
            binding.rvChatList.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // [4] 화면이 사라지기 전에 리스너 연결을 끊어줍니다. (가장 중요!)
        chatListListener?.remove()
        chatListListener = null

        _binding = null
    }
}