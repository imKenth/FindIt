package com.findit.app.presentation.home

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.databinding.FragmentHomeBinding
import com.findit.app.domain.model.ItemStatus
import com.findit.app.presentation.auth.AuthViewModel
import com.findit.app.data.repository.FirebaseAuthRepositoryImpl
import kotlinx.coroutines.launch

import androidx.core.os.bundleOf

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    }

    private val viewModel: HomeViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    }

    private lateinit var matchesAdapter: ItemAdapter
    private lateinit var recentAdapter: ItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
        observeViewModel()
        
        binding.reportLostCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reportTypeFragment)
        }
        
        binding.reportFoundCard.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reportTypeFragment)
        }

        binding.searchBar.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        binding.notificationButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
        }

        binding.seeAllMatches.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }

        binding.seeAllRecent.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun setupRecyclerViews() {
        matchesAdapter = ItemAdapter(isCompact = true) { item ->
            findNavController().navigate(R.id.action_homeFragment_to_itemDetailsFragment, bundleOf("itemId" to item.id))
        }
        binding.matchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = matchesAdapter
        }

        recentAdapter = ItemAdapter(isCompact = false) { item ->
            findNavController().navigate(R.id.action_homeFragment_to_itemDetailsFragment, bundleOf("itemId" to item.id))
        }
        binding.recentRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recentAdapter
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    authViewModel.currentUser.collect { user ->
                        binding.userNameText.text = "${user?.name ?: "User"} 👋"
                    }
                }
                launch {
                    viewModel.items.collect { items ->
                        val matches = items.filter { it.status == ItemStatus.FOUND }.take(4)
                        matchesAdapter.submitList(matches)
                        recentAdapter.submitList(items.take(6))
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
