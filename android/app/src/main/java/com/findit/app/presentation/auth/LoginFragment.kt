package com.findit.app.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.findit.app.R
import com.findit.app.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken: String? = account?.idToken
            val email = account?.email ?: return@registerForActivityResult
            val name = account.displayName ?: account.givenName ?: email.substringBefore("@").replace(".", " ")
            val photoUrl = account.photoUrl?.toString()

            if (!idToken.isNullOrEmpty()) {
                viewModel.signInWithGoogle(idToken, name, photoUrl)
            } else {
                viewModel.handleGoogleUser(name, email, photoUrl)
            }
        } catch (e: ApiException) {
            binding.errorText.text = "Google Sign-In Error (${e.statusCode}): Enable Google provider in Firebase Console."
            binding.errorText.isVisible = true
        } catch (e: Exception) {
            binding.errorText.text = e.localizedMessage ?: "Google Sign-In failed"
            binding.errorText.isVisible = true
        }
    }

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

        binding.googleSignInButton.setOnClickListener {
            binding.errorText.isVisible = false
            launchGoogleSignIn()
        }

        binding.createAccountButton.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        observeState()
    }

    private fun launchGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            val googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
            // Purge and revoke access so Google Play Services always forces the Account Chooser sheet
            googleSignInClient.signOut().addOnCompleteListener {
                googleSignInClient.revokeAccess().addOnCompleteListener {
                    if (isAdded) {
                        googleSignInLauncher.launch(googleSignInClient.signInIntent)
                    }
                }
            }
        } catch (e: Exception) {
            binding.errorText.text = e.localizedMessage ?: "Could not launch Google Sign-In"
            binding.errorText.isVisible = true
        }
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
                binding.googleSignInButton.isEnabled = false
            }
            is AuthState.Success -> {
                binding.signInButton.isEnabled = true
                binding.googleSignInButton.isEnabled = true
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
                binding.googleSignInButton.isEnabled = true
                binding.errorText.text = state.message
                binding.errorText.isVisible = true
            }
            is AuthState.Idle -> {
                binding.signInButton.isEnabled = true
                binding.googleSignInButton.isEnabled = true
                binding.errorText.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
