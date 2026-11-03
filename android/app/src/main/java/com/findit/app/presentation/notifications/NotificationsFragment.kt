package com.findit.app.presentation.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.findit.app.R
import com.findit.app.databinding.FragmentNotificationsBinding
import com.findit.app.domain.model.AppNotification

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.unreadCountCard.isVisible = false

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val adapter = NotificationsAdapter { notif ->
            notif.itemId?.let {
                findNavController().navigate(
                    R.id.action_notificationsFragment_to_itemDetailsFragment,
                    bundleOf("itemId" to it)
                )
            }
        }
        binding.notificationsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notificationsRecyclerView.adapter = adapter
        val notificationsList = emptyList<AppNotification>()
        adapter.submitList(notificationsList)

        binding.emptyState.isVisible = notificationsList.isEmpty()
        binding.notificationsRecyclerView.isVisible = notificationsList.isNotEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
