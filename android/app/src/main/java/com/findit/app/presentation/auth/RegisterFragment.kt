package com.findit.app.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.findit.app.R
import com.findit.app.databinding.FragmentRegisterBinding
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(com.findit.app.di.AppModule.authRepository) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createAccountButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val confirm = binding.confirmPasswordEditText.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                binding.errorText.text = "Please fill all required fields"
                binding.errorText.isVisible = true
            } else if (password != confirm) {
                binding.errorText.text = "Passwords do not match"
                binding.errorText.isVisible = true
            } else if (password.length < 8) {
                binding.errorText.text = "Password must be at least 8 characters"
                binding.errorText.isVisible = true
            } else {
                binding.errorText.isVisible = false
                viewModel.signUp(name, email, password)
            }
        }

        binding.signInLink.setOnClickListener {
            findNavController().popBackStack()
        }

        observeState()
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.authState.collect { state ->
                        handleAuthState(state)
                    }
                }
            }
        }
    }

    private fun handleAuthState(state: AuthState) {
        when (state) {
            is AuthState.Loading -> {
                binding.createAccountButton.isEnabled = false
            }
            is AuthState.Success -> {
                binding.createAccountButton.isEnabled = true
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = viewModel.currentUser.value
                    if (user?.email == "admin@school.edu") {
                        findNavController().navigate(R.id.action_registerFragment_to_adminDashboardFragment)
                    } else {
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    }
                }
            }
            is AuthState.Error -> {
                binding.createAccountButton.isEnabled = true
                binding.errorText.text = state.message
                binding.errorText.isVisible = true
            }
            is AuthState.Idle -> {
                binding.createAccountButton.isEnabled = true
                binding.errorText.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
