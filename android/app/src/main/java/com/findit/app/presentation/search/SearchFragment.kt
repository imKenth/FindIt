package com.findit.app.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.data.remote.MockData
import com.findit.app.databinding.FragmentSearchBinding
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemCategory
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.presentation.home.ItemAdapter
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by activityViewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(com.findit.app.di.AppModule.itemRepository) as T
            }
        }
    }

    private lateinit var adapter: ItemAdapter
    private var currentFilter = "All"
    private var currentCategory = "All"
    private var currentQuery = ""
    private var indexedItems = emptyList<SearchIndexEntry>()
    private var searchJob: Job? = null

    private data class SearchIndexEntry(
        val item: Item,
        val searchableFields: List<String>,
        val type: String,
        val category: String
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupChips()
        setupSearch()
        observeViewModel()
        
        binding.clearFiltersButton.setOnClickListener {
            binding.searchEditText.setText("")
            binding.chipAll.isChecked = true
            updateResults()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.items.collect { items ->
                    val sourceItems = if (items.isNotEmpty()) items else MockData.items
                    indexedItems = withContext(Dispatchers.Default) {
                        sourceItems.map { item ->
                            SearchIndexEntry(
                                item = item,
                                searchableFields = listOf(
                                    item.name,
                                    item.location,
                                    item.category.name,
                                    item.description
                                ).map { it.lowercase(Locale.ROOT) },
                                type = item.type.name,
                                category = item.category.name.replace("_", " ")
                            )
                        }
                    }
                    updateResults()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter(isCompact = false) { item ->
            findNavController().navigate(
                R.id.action_searchFragment_to_itemDetailsFragment,
                bundleOf("itemId" to item.id)
            )
        }
        binding.resultsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.resultsRecyclerView.adapter = adapter
    }

    private fun setupChips() {
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
            val checkedId = checkedIds.firstOrNull()
            currentFilter = when (checkedId) {
                R.id.chip_lost -> "Lost"
                R.id.chip_found -> "Found"
                else -> "All"
            }
            updateResults()
        }

        val categories = listOf("All") + ItemCategory.entries.dropLast(1).map { it.name.replace("_", " ") }
        categories.forEach { cat ->
            val chip = Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = cat == "All"
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        currentCategory = cat
                        updateResults()
                    }
                }
            }
            binding.categoryChipGroup.addView(chip)
        }
    }

    private fun setupSearch() {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            currentQuery = text.toString()
            updateResults()
        }
    }

    private fun updateResults() {
        val query = currentQuery.trim().lowercase(Locale.ROOT)
        val filter = currentFilter.uppercase(Locale.ROOT)
        val category = currentCategory
        val source = indexedItems
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            val results = withContext(Dispatchers.Default) {
                buildList {
                    for ((index, entry) in source.withIndex()) {
                        if (index % 64 == 0) currentCoroutineContext().ensureActive()
                        if (
                            (query.isEmpty() || entry.searchableFields.any { it.contains(query) }) &&
                            (filter == "ALL" || entry.type == filter) &&
                            (category == "All" || entry.category == category)
                        ) {
                            add(entry.item)
                        }
                    }
                }
            }

            adapter.submitList(results)
            binding.emptyState.isVisible = results.isEmpty()
            binding.resultsRecyclerView.isVisible = results.isNotEmpty()
        }
    }

    override fun onDestroyView() {
        searchJob?.cancel()
        searchJob = null
        super.onDestroyView()
        _binding = null
    }
}
