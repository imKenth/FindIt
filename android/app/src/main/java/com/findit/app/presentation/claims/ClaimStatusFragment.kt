package com.findit.app.presentation.claims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.findit.app.databinding.FragmentClaimStatusBinding
import com.findit.app.presentation.common.ItemDetailsViewModel
import kotlinx.coroutines.launch

class ClaimStatusFragment : Fragment() {

    private var _binding: FragmentClaimStatusBinding? = null
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
        _binding = FragmentClaimStatusBinding.inflate(inflater, container, false)
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

        binding.doneButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.item.collect { item ->
                    if (item != null) {
                        binding.itemNameText.text = item.name
                        binding.itemLocationText.text = "${item.location} • ${item.date}"
                        binding.claimReasonText.text = item.description
                        binding.claimStatusBadge.text = item.status.name
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
