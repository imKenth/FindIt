package com.findit.app.presentation.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.findit.app.R
import com.findit.app.databinding.FragmentReportTypeBinding

class ReportTypeFragment : Fragment() {

    private var _binding: FragmentReportTypeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportTypeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lostTypeCard.setOnClickListener {
            findNavController().navigate(R.id.action_reportTypeFragment_to_reportLostFragment)
        }

        binding.foundTypeCard.setOnClickListener {
            findNavController().navigate(R.id.action_reportTypeFragment_to_reportFoundFragment)
        }

        binding.cancelButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
