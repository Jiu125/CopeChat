package com.example.copechat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.R
import com.example.copechat.databinding.ItemMentorSearchBinding
import com.example.copechat.model.Mentor

class MentorListAdapter(
    private var items: List<Mentor>,
    private val onRequestClick: (Mentor) -> Unit,
    private val onItemClick: (Mentor) -> Unit,
    private val onMessageClick: ((Mentor) -> Unit)? = null
) : RecyclerView.Adapter<MentorListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMentorSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMentorSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMentorName.text = item.name
            tvMentorInfo.text = "${item.school} • ${item.major}"
            tvRating.text = item.rating
            tvReviewCount.text = "(${item.reviewCount})"
            tvIntroduction.text = item.introduction

            Glide.with(root.context)
                .load(item.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.bg_circle_placeholder)
                .into(ivMentorProfile)

            layoutBadge.visibility = if (item.isVerified) android.view.View.VISIBLE else android.view.View.GONE

            btnMessage.setOnClickListener {
                onRequestClick(item)
            }

            root.setOnClickListener {
                onItemClick(item)
            }

            btnMessage.setOnClickListener {
                onMessageClick?.invoke(item)
            }
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(newItems: List<Mentor>) {
        items = newItems
        notifyDataSetChanged()
    }
}