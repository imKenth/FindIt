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
import androidx.navigation.fragment.findNavController
import com.findit.app.data.repository.FirebaseAuthRepositoryImpl
import com.findit.app.R
import com.findit.app.databinding.FragmentLoginBinding
import com.findit.app.domain.model.User
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(FirebaseAuthRepositoryImpl()) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isBlank() || password.isBlank()) {
                binding.errorText.text = "Please enter both email and password"
                binding.errorText.isVisible = true
            } else {
                binding.errorText.isVisible = false
                viewModel.signIn(email, password)
            }
        }

        binding.createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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
                binding.signInButton.isEnabled = false
                // Could add a progress indicator in XML and show it here
            }
            is AuthState.Success -> {
                binding.signInButton.isEnabled = true
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = viewModel.currentUser.value
                    if (user?.email == "admin@school.edu") {
                        findNavController().navigate(R.id.action_loginFragment_to_adminDashboardFragment)
                    } else {
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    }
                }
            }
            is AuthState.Error -> {
                binding.signInButton.isEnabled = true
                binding.errorText.text = state.message
                binding.errorText.isVisible = true
            }
            is AuthState.Idle -> {
                binding.signInButton.isEnabled = true
                binding.errorText.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
