package com.findit.app.presentation.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.data.remote.MockData
import com.findit.app.databinding.FragmentSearchBinding
import com.findit.app.domain.model.ItemCategory
import com.findit.app.presentation.home.ItemAdapter
import com.google.android.material.chip.Chip

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ItemAdapter
    private var currentFilter = "All"
    private var currentCategory = "All"
    private var currentQuery = ""

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
        
        binding.clearFiltersButton.setOnClickListener {
            binding.searchEditText.setText("")
            binding.chipAll.isChecked = true
            // Reset category selection programmatically is handled in chip listener
            updateResults()
        }

        updateResults()
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
        binding.filterChipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
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
        val results = MockData.items.filter { item ->
            val q = currentQuery.lowercase()
            val matchQ = q.isEmpty() || item.name.lowercase().contains(q) || 
                         item.location.lowercase().contains(q) || 
                         item.category.name.lowercase().contains(q)
            val matchF = (currentFilter == "All") || (item.type.name == currentFilter.uppercase())
            val matchC = currentCategory == "All" || item.category.name.replace("_", " ") == currentCategory
            matchQ && matchF && matchC
        }

        adapter.submitList(results)
        binding.emptyState.isVisible = results.isEmpty()
        binding.resultsRecyclerView.isVisible = results.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
