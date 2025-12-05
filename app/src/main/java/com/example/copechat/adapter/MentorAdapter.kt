package com.example.copechat.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.R
import com.example.copechat.databinding.ItemMentorCardBinding
import com.example.copechat.model.Mentor

class MentorAdapter(
    private var items: List<Mentor>,
    private val onItemClick: ((Mentor) -> Unit)? = null
) : RecyclerView.Adapter<MentorAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMentorCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMentorCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvMentorName.text = item.name
            tvMentorSchool.text = item.school
            tvMentorMajor.text = item.major
            tvRatingScore.text = item.rating
            tvRatingCount.text = item.reviewCount

            Glide.with(root.context)
                .load(item.imageUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .into(ivMentorProfile)

            ivBadge.visibility = if (item.isVerified) View.VISIBLE else View.GONE

            if (item.tags.isNotEmpty()) tvTag1.text = item.tags[0]
            if (item.tags.size > 1) tvTag2.text = item.tags[1]

            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<Mentor>) {
        this.items = newItems
        notifyDataSetChanged()
    }
}