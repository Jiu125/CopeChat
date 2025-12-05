package com.example.copechat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.copechat.R
import com.example.copechat.databinding.ItemActivityCardBinding
import com.example.copechat.model.ActivityItem
import com.example.copechat.model.ActivityStatus
import com.example.copechat.model.ActivityType

class ActivityAdapter(
    private var items: List<ActivityItem>,
    private val onItemClick: ((ActivityItem) -> Unit)? = null
) : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemActivityCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemActivityCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvActivityTitle.text = item.title
            tvActivitySubtitle.text = item.subtitle
            tvActivityTime.text = item.time

            if (item.type == ActivityType.MENTORING) {
                viewIconBg.background.setTint(ContextCompat.getColor(context, R.color.bg_mentoring))
                ivActivityIcon.setImageResource(android.R.drawable.ic_menu_camera)
                ivActivityIcon.setColorFilter(ContextCompat.getColor(context, R.color.tint_mentoring))
            } else {
                viewIconBg.background.setTint(ContextCompat.getColor(context, R.color.bg_translation))
                ivActivityIcon.setImageResource(android.R.drawable.ic_menu_edit)
                ivActivityIcon.setColorFilter(ContextCompat.getColor(context, R.color.tint_translation))
            }

            if (item.status == ActivityStatus.UPCOMING) {
                val color = ContextCompat.getColor(context, R.color.status_upcoming)
                tvActivityTime.setTextColor(color)
                ivTimeIcon.setColorFilter(color)
                ivTimeIcon.setImageResource(android.R.drawable.ic_menu_recent_history)
            } else {
                val color = ContextCompat.getColor(context, R.color.status_completed)
                tvActivityTime.setTextColor(color)
                ivTimeIcon.setColorFilter(color)
                ivTimeIcon.setImageResource(R.drawable.ic_outline_check_circle)
            }

            root.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }
    override fun getItemCount() = items.size

    fun updateList(newItems: List<ActivityItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}