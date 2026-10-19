package com.findit.app.presentation.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.findit.app.R
import com.findit.app.databinding.ItemNotificationBinding
import com.findit.app.domain.model.AppNotification

class NotificationsAdapter(
    private val onClick: (AppNotification) -> Unit
) : ListAdapter<AppNotification, NotificationsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notif: AppNotification) {
            binding.root.setOnClickListener { onClick(notif) }
            binding.notifTitle.text = notif.title
            binding.notifMessage.text = notif.message
            binding.notifTime.text = notif.time
            binding.unreadIndicator.isVisible = !notif.read
            
            val context = binding.root.context
            if (!notif.read) {
                binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.violet_50))
            } else {
                binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }

            val (emoji, bg) = when (notif.type) {
                "match" -> "💡" to R.color.violet_100
                "claim" -> "🏷️" to R.color.amber_100
                "approved" -> "✔️" to R.color.green_100
                "rejected" -> "✖" to R.color.red_100
                else -> "ℹ️" to R.color.blue_100
            }

            binding.notifEmoji.text = emoji
            binding.iconCard.setCardBackgroundColor(ContextCompat.getColor(context, bg))
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppNotification>() {
        override fun areItemsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AppNotification, newItem: AppNotification): Boolean = oldItem == newItem
    }
}
