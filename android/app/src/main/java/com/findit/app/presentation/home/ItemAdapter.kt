package com.findit.app.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.findit.app.R
import com.findit.app.databinding.CardItemCompactBinding
import com.findit.app.databinding.CardItemWideBinding
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemStatus

class ItemAdapter(
    private val isCompact: Boolean,
    private val onItemClick: (Item) -> Unit
) : ListAdapter<Item, RecyclerView.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (isCompact) {
            val binding = CardItemCompactBinding.inflate(inflater, parent, false)
            CompactViewHolder(binding)
        } else {
            val binding = CardItemWideBinding.inflate(inflater, parent, false)
            WideViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (holder is CompactViewHolder) {
            holder.bind(item)
        } else if (holder is WideViewHolder) {
            holder.bind(item)
        }
    }

    inner class CompactViewHolder(private val binding: CardItemCompactBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            val fallbackImage = "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format"
            binding.root.setOnClickListener { onItemClick(item) }
            binding.itemImage.load(item.imageUrl.ifEmpty { fallbackImage }) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
            binding.itemName.text = item.name
            binding.itemLocation.text = item.location
            binding.statusBadge.text = item.status.name
            
            val context = binding.root.context
            when (item.status) {
                ItemStatus.LOST -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_red)
                }
                ItemStatus.FOUND -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_green)
                }
                ItemStatus.PENDING -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_amber)
                }
                ItemStatus.APPROVED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.blue_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_blue)
                }
                ItemStatus.CLAIMED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.violet_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_violet)
                }
                ItemStatus.RESOLVED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_emerald)
                }
                ItemStatus.REJECTED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.gray_500))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_gray)
                }
            }
        }
    }

    inner class WideViewHolder(private val binding: CardItemWideBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            val fallbackImage = "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format"
            binding.root.setOnClickListener { onItemClick(item) }
            binding.itemImage.load(item.imageUrl.ifEmpty { fallbackImage }) {
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }
            binding.itemName.text = item.name
            binding.itemCategory.text = item.category.name.replace("_", " ")
            binding.itemLocation.text = item.location
            binding.itemDate.text = item.date
            binding.statusBadge.text = item.status.name

            val context = binding.root.context
            when (item.status) {
                ItemStatus.LOST -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_red)
                }
                ItemStatus.FOUND -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_green)
                }
                ItemStatus.PENDING -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.amber_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_amber)
                }
                ItemStatus.APPROVED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.blue_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_blue)
                }
                ItemStatus.CLAIMED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.violet_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_violet)
                }
                ItemStatus.RESOLVED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_emerald)
                }
                ItemStatus.REJECTED -> {
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.gray_500))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_gray)
                }
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = oldItem == newItem
    }
}
