package com.example.copechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.databinding.ItemMentoringSessionBinding
import com.example.copechat.model.MentoringSession

class MentoringSessionAdapter(
    private var items: List<MentoringSession>,
    private val onEnterClick: (MentoringSession) -> Unit,
    private val onMessageClick: (MentoringSession) -> Unit,
    private val onProfileClick: ((MentoringSession) -> Unit)? = null
) : RecyclerView.Adapter<MentoringSessionAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMentoringSessionBinding) : RecyclerView.ViewHolder(binding.root)

    fun updateData(newItems: List<MentoringSession>) {
        this.items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMentoringSessionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvSessionTitle.text = item.sessionTitle
            tvSessionTime.text = "${item.date} | ${item.time}"
            tvMentorName.text = item.mentorName
            tvDDay.text = item.dDay

            Glide.with(root.context)
                .load(item.mentorProfileUrl)
                .circleCrop()
                .into(ivMentorProfile)

            btnEnter.isEnabled = item.isEnterEnabled
            btnEnter.alpha = if (item.isEnterEnabled) 1.0f else 0.5f

            btnEnter.setOnClickListener { onEnterClick(item) }
            btnMessage.setOnClickListener { onMessageClick(item) }

            ivMentorProfile.setOnClickListener {
                onProfileClick?.invoke(item)
            }
        }
    }

    override fun getItemCount() = items.size
}