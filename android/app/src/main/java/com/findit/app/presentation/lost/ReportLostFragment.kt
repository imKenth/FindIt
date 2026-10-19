package com.findit.app.presentation.lost

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.findit.app.data.repository.FirebaseAuthRepositoryImpl
import com.findit.app.data.repository.FirestoreRepositoryImpl
import com.findit.app.databinding.FragmentReportLostBinding
import com.findit.app.domain.model.ItemCategory
import com.findit.app.domain.model.ItemType
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.presentation.report.ReportState
import com.findit.app.presentation.report.ReportViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ReportLostFragment : Fragment() {

    private var _binding: FragmentReportLostBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    }

    private val reportViewModel: ReportViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ReportViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportLostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryDropdown()
        
        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.closeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.submitButton.setOnClickListener {
            submitReport()
        }

        binding.backToHomeButton.setOnClickListener {
            findNavController().popBackStack()
        }

        observeState()
    }

    private fun setupCategoryDropdown() {
        val categories = ItemCategory.values().map { it.name.replace("_", " ") }
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_dropdown_item_1line, categories)
        binding.categoryDropdown.setAdapter(adapter)
    }

    private fun submitReport() {
        val name = binding.nameEditText.text.toString()
        val category = binding.categoryDropdown.text.toString()
        val location = binding.locationEditText.text.toString()
        val date = binding.dateEditText.text.toString()
        val time = binding.timeEditText.text.toString()
        val description = binding.descriptionEditText.text.toString()

        val finalCategory = category.ifBlank { "Other" }
        val finalDate = date.ifBlank { "Today" }
        val finalDesc = description.ifBlank { "No detailed description provided." }

        if (name.isBlank() || location.isBlank()) {
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val user = authViewModel.currentUser.value
            reportViewModel.reportItem(
                name = name,
                category = finalCategory,
                location = location,
                date = finalDate,
                time = time.ifBlank { null },
                description = finalDesc,
                type = ItemType.LOST,
                reportedBy = user?.name ?: "User"
            )
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                reportViewModel.reportState.collect { state ->
                    handleReportState(state)
                }
            }
        }
    }

    private fun handleReportState(state: ReportState) {
        when (state) {
            is ReportState.Loading -> {
                binding.submitButton.isEnabled = false
                // Show a progress indicator if added to XML
            }
            is ReportState.Success -> {
                binding.successOverlay.isVisible = true
            }
            is ReportState.Error -> {
                binding.submitButton.isEnabled = true
                // Show error message
            }
            is ReportState.Idle -> {
                binding.submitButton.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
