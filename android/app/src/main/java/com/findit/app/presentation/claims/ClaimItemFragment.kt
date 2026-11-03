package com.findit.app.presentation.claims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.findit.app.databinding.FragmentClaimItemBinding
import com.findit.app.presentation.common.ItemDetailsViewModel
import kotlinx.coroutines.launch

class ClaimItemFragment : Fragment() {

    private var _binding: FragmentClaimItemBinding? = null
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
        _binding = FragmentClaimItemBinding.inflate(inflater, container, false)
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

        binding.submitClaimButton.setOnClickListener {
            submitClaim()
        }

        binding.backToHomeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.item.collect { item ->
                    item?.let {
                        binding.itemNameText.text = it.name
                        binding.itemLocationText.text = "${it.location} • ${it.date}"
                    }
                }
            }
        }
    }

    private fun submitClaim() {
        val reason = binding.reasonEditText.text.toString().trim()
        val dateLost = binding.dateLostEditText.text.toString().trim()

        if (reason.isEmpty() || dateLost.isEmpty()) {
            binding.errorText.text = "Please fill in all required fields (*)"
            binding.errorText.isVisible = true
        } else {
            binding.errorText.isVisible = false
            binding.successOverlay.isVisible = true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
