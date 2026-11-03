package com.findit.app.presentation.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.findit.app.R
import com.findit.app.data.remote.MockData
import com.findit.app.databinding.*
import com.findit.app.presentation.home.HomeViewModel
import com.findit.app.presentation.home.ItemAdapter
import kotlinx.coroutines.launch

class AdminDashboardTabFragment : Fragment() {
    private var _binding: FragmentAdminDashboardTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminDashboardTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.activityRecyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = ActivityAdapter()
        binding.activityRecyclerView.adapter = adapter
        adapter.setItems(MockData.adminActivity)
    }

    private class ActivityAdapter : RecyclerView.Adapter<ActivityAdapter.ViewHolder>() {
        private var items = emptyList<Map<String, String>>()

        fun setItems(newItems: List<Map<String, String>>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemAdminActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(private val binding: ItemAdminActivityBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Map<String, String>) {
                binding.activityAction.text = data["action"]
                binding.activityItem.text = data["item"]
                binding.activityUserTime.text = "${data["user"]} · ${data["time"]}"

                val context = binding.root.context
                val (icon, bg, text) = when (data["type"]) {
                    "approved" -> Triple("✔", R.color.green_100, R.color.green_600)
                    "report" -> Triple("+", R.color.violet_100, R.color.violet_600)
                    "flag" -> Triple("⚠", R.color.amber_100, R.color.amber_600)
                    "rejected" -> Triple("✖", R.color.red_100, R.color.red_500)
                    else -> Triple("•", R.color.gray_100, R.color.gray_500)
                }
                binding.activityIcon.text = icon
                binding.iconCard.setCardBackgroundColor(ContextCompat.getColor(context, bg))
                binding.activityIcon.setTextColor(ContextCompat.getColor(context, text))
            }
        }
    }
}

class AdminReportsTabFragment : Fragment() {
    private var _binding: FragmentAdminReportsTabBinding? = null
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminReportsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ItemAdapter(isCompact = false) { _ -> }
        binding.reportsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.reportsRecyclerView.adapter = adapter

        observeViewModel()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.items.collect { items ->
                    val displayItems = if (items.isNotEmpty()) items else MockData.items
                    binding.reportsCountText.text = "${displayItems.size} total reports"
                    adapter.submitList(displayItems)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AdminClaimsTabFragment : Fragment() {
    private var _binding: FragmentAdminClaimsTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminClaimsTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.claimsCountText.text = "${MockData.adminClaims.size} pending claims"
        binding.claimsRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = AdminClaimsAdapter()
        binding.claimsRecyclerView.adapter = adapter
        adapter.setClaims(MockData.adminClaims)
    }

    private class AdminClaimsAdapter : RecyclerView.Adapter<AdminClaimsAdapter.ViewHolder>() {
        private var items = emptyList<Map<String, String>>()

        fun setClaims(newItems: List<Map<String, String>>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = CardAdminClaimBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(private val binding: CardAdminClaimBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Map<String, String>) {
                binding.itemName.text = data["item"]
                binding.claimantInfo.text = "Claimant: ${data["claimant"]} · ${data["when"]}"
                binding.claimDetail.text = data["detail"]

                val context = binding.root.context
                binding.approveButton.setOnClickListener {
                    binding.actionLayout.isVisible = false
                    binding.statusMessage.isVisible = true
                    binding.statusMessage.text = "✔ Approved — notifying claimant to collect item"
                    binding.statusMessage.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                    binding.statusBadge.text = "APPROVED"
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_green)
                }

                binding.rejectButton.setOnClickListener {
                    binding.actionLayout.isVisible = false
                    binding.statusMessage.isVisible = true
                    binding.statusMessage.text = "✖ Rejected — claimant will be notified"
                    binding.statusMessage.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                    binding.statusBadge.text = "REJECTED"
                    binding.statusBadge.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                    binding.statusBadge.setBackgroundResource(R.drawable.bg_badge_red)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

class AdminUsersTabFragment : Fragment() {
    private var _binding: FragmentAdminUsersTabBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAdminUsersTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.usersCountText.text = "${MockData.adminUsers.size} registered users"
        binding.usersRecyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = AdminUsersAdapter()
        binding.usersRecyclerView.adapter = adapter
        adapter.setUsers(MockData.adminUsers)
    }

    private class AdminUsersAdapter : RecyclerView.Adapter<AdminUsersAdapter.ViewHolder>() {
        private var items = emptyList<Map<String, Any>>()

        fun setUsers(newItems: List<Map<String, Any>>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = CardAdminUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(items[position])
        }

        override fun getItemCount(): Int = items.size

        class ViewHolder(private val binding: CardAdminUserBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(data: Map<String, Any>) {
                val name = data["name"]?.toString() ?: "User"
                binding.userInitial.text = name.take(1).uppercase()
                binding.userName.text = name
                binding.userEmail.text = data["email"]?.toString()
                binding.userReports.text = "${data["reports"]} reports"
                binding.statusText.text = data["status"]?.toString()

                val context = binding.root.context
                val isSuspended = data["status"] == "Suspended"
                if (isSuspended) {
                    binding.statusCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.red_100))
                    binding.statusText.setTextColor(ContextCompat.getColor(context, R.color.red_600))
                    binding.actionText.text = "Activate"
                } else {
                    binding.statusCard.setCardBackgroundColor(ContextCompat.getColor(context, R.color.green_100))
                    binding.statusText.setTextColor(ContextCompat.getColor(context, R.color.green_600))
                    binding.actionText.text = "Suspend"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
