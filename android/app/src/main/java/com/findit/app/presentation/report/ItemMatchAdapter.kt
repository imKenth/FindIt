package com.findit.app.presentation.report

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.findit.app.databinding.CardItemMatchBinding
import com.findit.app.domain.matching.ItemMatch

class ItemMatchAdapter(
    private val onItemClick: (ItemMatch) -> Unit
) : ListAdapter<ItemMatch, ItemMatchAdapter.MatchViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val binding = CardItemMatchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MatchViewHolder(
        private val binding: CardItemMatchBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(match: ItemMatch) {
            binding.root.setOnClickListener { onItemClick(match) }
            binding.matchItemName.text = match.item.name
            binding.matchItemStatus.text = match.item.status.name
            binding.matchItemLocation.text = match.item.location
            binding.matchReasons.text = match.reasons.joinToString(" · ")
        }
    }

    private companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<ItemMatch>() {
            override fun areItemsTheSame(oldItem: ItemMatch, newItem: ItemMatch): Boolean =
                oldItem.item.id == newItem.item.id

            override fun areContentsTheSame(oldItem: ItemMatch, newItem: ItemMatch): Boolean =
                oldItem == newItem
        }
    }
}
