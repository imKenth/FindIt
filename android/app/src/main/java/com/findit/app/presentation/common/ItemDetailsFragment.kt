package com.findit.app.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
import com.findit.app.R
import com.findit.app.databinding.FragmentItemDetailsBinding
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemStatus
import kotlinx.coroutines.launch

class ItemDetailsFragment : Fragment() {

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ItemDetailsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ItemDetailsViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val itemId = arguments?.getString("itemId") ?: ""
        if (itemId.isNotEmpty()) {
            viewModel.getItem(itemId)
        }
        
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.item.collect { item ->
                    item?.let { bindItem(it) }
                }
            }
        }
    }

    private fun bindItem(item: Item) {
        binding.itemHeroImage.load(item.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1512054502232-10a0a035d672?w=400&h=300&fit=crop&auto=format" })
        binding.itemName.text = item.name
        binding.categoryText.text = item.category.name.replace("_", " ")
        binding.itemDescription.text = item.description
        
        // Meta Rows
        binding.locationMeta.metaEmoji.text = "📍"
        binding.locationMeta.metaLabel.text = "Location"
        binding.locationMeta.metaValue.text = item.location

        binding.dateMeta.metaEmoji.text = "📅"
        binding.dateMeta.metaLabel.text = "Date Reported"
        binding.dateMeta.metaValue.text = "${item.date}${item.time?.let { " · $it" } ?: ""}"

        binding.reporterMeta.metaEmoji.text = "👤"
        binding.reporterMeta.metaLabel.text = "Reported By"
        binding.reporterMeta.metaValue.text = item.reportedBy

        // Status Badge
        binding.statusBadge.text = item.status.name
        val context = requireContext()
        when (item.status) {
            ItemStatus.LOST -> {
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_red)
            }
            ItemStatus.FOUND -> {
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_green)
            }
            ItemStatus.CLAIMED -> {
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.violet_600))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_violet)
            }
            ItemStatus.RESOLVED -> {
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.emerald_600))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_emerald)
            }
            else -> {
                binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.gray_500))
                binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_gray)
            }
        }

        val canClaim = (item.status == ItemStatus.LOST) || (item.status == ItemStatus.FOUND)
        binding.claimButton.isEnabled = canClaim
        if (!canClaim) {
            binding.claimButton.text = "Item is ${item.status.name} — not available"
            binding.claimButton.alpha = 0.5f
        } else {
            binding.claimButton.text = "Claim This Item"
            binding.claimButton.alpha = 1.0f
        }
        
        binding.claimButton.setOnClickListener {
            findNavController().navigate(
                R.id.action_itemDetailsFragment_to_claimItemFragment,
                bundleOf("itemId" to item.id)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
