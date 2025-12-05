package com.example.copechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.databinding.ItemChatMessageMeBinding
import com.example.copechat.databinding.ItemChatMessageOtherBinding
import com.example.copechat.model.ChatMessageModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomAdapter(
    private val messages: List<ChatMessageModel>,
    private val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_ME = 1
        private const val VIEW_TYPE_OTHER = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) VIEW_TYPE_ME else VIEW_TYPE_OTHER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ME) {
            val binding = ItemChatMessageMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            MeViewHolder(binding)
        } else {
            val binding = ItemChatMessageOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            OtherViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        if (holder is MeViewHolder) {
            holder.bind(message)
        } else if (holder is OtherViewHolder) {
            holder.bind(message)
        }
    }

    override fun getItemCount() = messages.size

    // [추가] 날짜를 "오후 2:30" 형식으로 변환하는 함수
    private fun formatTime(date: Date?): String {
        // date가 null이면(전송 중이면) 현재 시간이나 "..." 등으로 표시
        val actualDate = date ?: Date()
        val format = SimpleDateFormat("a h:mm", Locale.KOREA)
        return format.format(actualDate)
    }

    inner class MeViewHolder(val binding: ItemChatMessageMeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatMessageModel) {
            binding.tvMessage.text = item.message
            binding.tvTime.text = formatTime(item.timestamp) // 시간 포맷 적용
        }
    }

    inner class OtherViewHolder(val binding: ItemChatMessageOtherBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatMessageModel) {
            binding.tvMessage.text = item.message
            binding.tvTime.text = formatTime(item.timestamp)

            // 상대방 프로필 (없으면 기본 이미지)
            Glide.with(binding.root.context)
                .load(item.senderProfileUrl)
                .circleCrop()
                .into(binding.ivProfile)
        }
    }
}