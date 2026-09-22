package com.findit.app.presentation.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.data.remote.MockData
import com.findit.app.databinding.FragmentMyReportsBinding
import com.findit.app.domain.model.Item
import com.findit.app.presentation.home.ItemAdapter
import com.google.android.material.tabs.TabLayout

class MyReportsFragment : Fragment() {

    private var _binding: FragmentMyReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ItemAdapter

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

        updateList(0)
    }

    private fun setupRecyclerView() {
        binding.reportsRecyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                updateList(tab?.position ?: 0)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        
        binding.createReportFab.setOnClickListener {
            findNavController().navigate(R.id.action_myReportsFragment_to_reportTypeFragment)
        }
    }

    private fun updateList(position: Int) {
        val items = when (position) {
            0 -> MockData.myReports
            1 -> MockData.myClaims
            2 -> MockData.resolvedReports
            else -> emptyList()
        }
        
        // Use different navigation for claims
        adapter = ItemAdapter(isCompact = false) { item ->
            if (position == 1) {
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
        
        adapter.submitList(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
