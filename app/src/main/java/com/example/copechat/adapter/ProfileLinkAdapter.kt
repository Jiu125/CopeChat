package com.example.copechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.copechat.databinding.ItemRichLinkBinding
import com.example.copechat.model.ProfileLink

class ProfileLinkAdapter(
    private val items: List<ProfileLink>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<ProfileLinkAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemRichLinkBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRichLinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvLinkTitle.text = item.title
            tvLinkDesc.text = item.description
            tvLinkDomain.text = item.domain

            // Glide로 썸네일 로드
            Glide.with(root.context)
                .load(item.thumbnail)
                .centerCrop()
                .into(ivThumbnail)

            // 클릭 리스너 (웹 브라우저로 이동 등)
            root.setOnClickListener {
                onItemClick(item.url)
            }
        }
    }

    override fun getItemCount() = items.size
}