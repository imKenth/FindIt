package com.findit.app.presentation.lost

import android.R
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import coil.load
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
    private var selectedImageUri: Uri? = null

    private val imagePicker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
        binding.photoPreview.isVisible = uri != null
        if (uri != null) {
            binding.photoPreview.load(uri)
            binding.photoButton.text = getString(com.findit.app.R.string.change_photo)
        } else {
            binding.photoButton.text = getString(com.findit.app.R.string.add_photo)
        }
    }

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

        binding.photoButton.setOnClickListener {
            imagePicker.launch("image/*")
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
            val firebaseUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            val user = authViewModel.currentUser.value
            val userEmail = firebaseUser?.email ?: user?.email ?: "user@school.edu"
            val userName = firebaseUser?.displayName ?: user?.name ?: userEmail.substringBefore("@")

            reportViewModel.reportItem(
                name = name,
                category = finalCategory,
                location = location,
                date = finalDate,
                time = time.ifBlank { null },
                description = finalDesc,
                type = ItemType.LOST,
                reportedBy = userName,
                reportedById = userEmail,
                imageUri = selectedImageUri
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
                binding.reportErrorText.isVisible = false
            }
            is ReportState.Success -> {
                binding.successOverlay.isVisible = true
            }
            is ReportState.Error -> {
                binding.submitButton.isEnabled = true
                binding.reportErrorText.text = state.message
                binding.reportErrorText.isVisible = true
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
