package com.findit.app.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.findit.app.R
import com.findit.app.databinding.FragmentProfileBinding
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.presentation.home.HomeViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val profileImagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        if (_binding == null) return@registerForActivityResult
        binding.profileImage.isEnabled = false
        binding.editProfileButton.isEnabled = false
        authViewModel.updateProfilePhoto(uri, requireContext().contentResolver) { result ->
            if (!isAdded || _binding == null) return@updateProfilePhoto
            binding.profileImage.isEnabled = true
            binding.editProfileButton.isEnabled = true
            result.onFailure {
                Toast.makeText(
                    requireContext(),
                    "Could not update profile photo",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private val homeViewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    }

    private var currentUserName = "User"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.profileImage.setImageResource(R.drawable.ic_profile_default)

        setupMenu()
        setupAdminPanel()
        observeData()

        binding.profileImage.setOnClickListener { openProfileImagePicker() }
        binding.editProfileButton.setOnClickListener { openProfileImagePicker() }

        binding.signOutButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_loginFragment)
        }
    }

    private fun openProfileImagePicker() {
        profileImagePicker.launch("image/*")
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.currentUser.collect { user ->
                        if (user != null) {
                            currentUserName = user.name
                            binding.profileImage.load(user.avatar) {
                                placeholder(R.drawable.ic_profile_default)
                                error(R.drawable.ic_profile_default)
                            }
                            binding.userName.text = user.name
                            binding.userEmail.text = user.email
                        }
                    }
                }

                launch {
                    homeViewModel.items.collect { items ->
                        val userItems = items.filter { it.reportedBy.equals(currentUserName, ignoreCase = true) }
                        val activeCount = userItems.count { it.status == ItemStatus.LOST || it.status == ItemStatus.FOUND }
                        val claimsCount = userItems.count { it.status == ItemStatus.CLAIMED }
                        val resolvedCount = userItems.count { it.status == ItemStatus.RESOLVED }

                        binding.reportsCount.text = activeCount.toString()
                        binding.claimsCount.text = claimsCount.toString()
                        binding.resolvedCount.text = resolvedCount.toString()

                        binding.menuReports.menuSubtitle.text = "$activeCount active reports"
                    }
                }
            }
        }
    }

    private fun setupAdminPanel() {
        binding.adminPanelCard.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_adminDashboardFragment)
        }
    }

    private fun setupMenu() {
        binding.menuReports.menuIcon.text = "📋"
        binding.menuReports.menuLabel.text = "My Reports"
        binding.menuReports.menuSubtitle.text = "0 active reports"
        binding.menuReports.root.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_myReportsFragment)
        }

        binding.menuNotifications.menuIcon.text = "🔔"
        binding.menuNotifications.menuLabel.text = "Notifications"
        binding.menuNotifications.menuSubtitle.text = "All caught up"
        binding.menuNotifications.root.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_notificationsFragment)
        }

        binding.menuSettings.menuIcon.text = "⚙️"
        binding.menuSettings.menuLabel.text = "Settings"
        binding.menuSettings.menuSubtitle.text = "Privacy & preferences"

        binding.menuHelpSupport.menuIcon.text = "❓"
        binding.menuHelpSupport.menuLabel.text = "Help & Support"
        binding.menuHelpSupport.menuSubtitle.text = "FAQs, contact admin"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
