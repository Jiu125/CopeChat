package com.example.copechat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.R
import com.example.copechat.databinding.ItemChatRoomBinding
import com.example.copechat.model.ChatRoomModel
import java.text.SimpleDateFormat
import java.util.Locale

class ChatListAdapter(
    private val items: List<ChatRoomModel>,
    private val onItemClick: (ChatRoomModel) -> Unit // 클릭 이벤트 콜백
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemChatRoomBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatRoomBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvName.text = item.name
            tvLastMessage.text = item.lastMessage

            val sdf = SimpleDateFormat("a hh:mm", Locale.getDefault())

            if (item.timestamp != null) {
                tvTimestamp.text = sdf.format(item.timestamp)
            } else {
                tvTimestamp.text = "" // 만약 시간이 없다면 빈 문자열을 표시
            }

            // 읽지 않은 메시지가 0이면 숨김
            if (item.unreadCount > 0) {
                tvUnreadCount.text = item.unreadCount.toString()
                tvUnreadCount.visibility = View.VISIBLE
            } else {
                tvUnreadCount.visibility = View.GONE
            }

            Glide.with(root.context)
                .load(item.profileImageUrl)
                .circleCrop()
                .placeholder(R.drawable.bg_circle_placeholder)
                .into(ivProfile)

            // 아이템 클릭 시 ChatRoomFragment로 이동하도록 설정
            root.setOnClickListener { onItemClick(item) }
        }
    }
    override fun getItemCount() = items.size
}