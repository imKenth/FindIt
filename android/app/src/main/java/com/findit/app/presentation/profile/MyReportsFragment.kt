package com.findit.app.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.databinding.FragmentMyReportsBinding
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.presentation.home.ItemAdapter
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch

class MyReportsFragment : Fragment() {

    private var _binding: FragmentMyReportsBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
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

    private lateinit var adapter: ItemAdapter
    private var allItemsList = emptyList<Item>()
    private var currentUserName = "User"
    private var currentUserEmail = ""
    private var currentTab = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.currentUser.collect { user ->
                        if (user != null) {
                            currentUserName = user.name
                            currentUserEmail = user.email
                            updateList(currentTab)
                        }
                    }
                }
                launch {
                    homeViewModel.items.collect { items ->
                        allItemsList = items
                        updateList(currentTab)
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.reportsRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ItemAdapter(isCompact = false) { item ->
            if (currentTab == 1) {
                findNavController().navigate(
                    R.id.action_myReportsFragment_to_claimStatusFragment,
                    bundleOf("itemId" to item.id)
                )
            } else {
                findNavController().navigate(
                    R.id.action_myReportsFragment_to_itemDetailsFragment,
                    bundleOf("itemId" to item.id)
                )
            }
        }
        binding.reportsRecyclerView.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                updateList(currentTab)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.createReportFab.setOnClickListener {
            findNavController().navigate(R.id.action_myReportsFragment_to_reportTypeFragment)
        }
    }

    private fun updateList(position: Int) {
        val userItems = allItemsList.filter { item ->
            val emailMatch = currentUserEmail.isNotBlank() && item.reportedById.equals(currentUserEmail, ignoreCase = true)
            val legacyNameMatch = item.reportedById.isBlank() && currentUserName.isNotBlank() && currentUserName != "User" && item.reportedBy.equals(currentUserName, ignoreCase = true)

            emailMatch || legacyNameMatch
        }

        val items = when (position) {
            0 -> userItems.filter { item -> item.status == ItemStatus.LOST || item.status == ItemStatus.FOUND }
            1 -> userItems.filter { item -> item.status == ItemStatus.CLAIMED || item.status == ItemStatus.PENDING }
            2 -> userItems.filter { item -> item.status == ItemStatus.RESOLVED }
            else -> emptyList()
        }

        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
