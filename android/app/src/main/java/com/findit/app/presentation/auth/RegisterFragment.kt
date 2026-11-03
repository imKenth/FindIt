package com.findit.app.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.findit.app.data.repository.AuthEmailPolicy
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.findit.app.R
import com.findit.app.databinding.FragmentRegisterBinding
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account?.idToken
            val email = account?.email ?: return@registerForActivityResult
            val name = account.displayName ?: account.givenName ?: email.substringBefore("@").replace(".", " ")
            val photoUrl = account.photoUrl?.toString()

            if (!idToken.isNullOrEmpty()) {
                viewModel.signInWithGoogle(idToken, email, name, photoUrl)
            } else {
                viewModel.handleGoogleUser(email)
            }
        } catch (e: ApiException) {
            binding.errorText.text = "Google Sign-Up Error (${e.statusCode}): Enable Google provider in Firebase Console."
            binding.errorText.isVisible = true
        } catch (e: Exception) {
            binding.errorText.text = e.localizedMessage ?: "Google Sign-Up failed"
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

        binding.googleRegisterButton.setOnClickListener {
            binding.errorText.isVisible = false
            launchGoogleSignIn()
        }

        binding.signInLink.setOnClickListener {
            findNavController().popBackStack()
        }

        observeState()
    }

    private fun launchGoogleSignIn() {
        try {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .setHostedDomain("sjp2cd.edu.ph")
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
                binding.createAccountButton.isEnabled = false
                binding.googleRegisterButton.isEnabled = false
            }
            is AuthState.Success -> {
                binding.createAccountButton.isEnabled = true
                binding.googleRegisterButton.isEnabled = true
                viewLifecycleOwner.lifecycleScope.launch {
                    val user = viewModel.currentUser.value
                    if (user?.email == "admin@school.edu") {
                        findNavController().navigate(R.id.action_registerFragment_to_adminDashboardFragment)
                    } else {
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    }
                }
            }
            is AuthState.VerificationEmailSent -> {
                binding.createAccountButton.isEnabled = true
                binding.googleRegisterButton.isEnabled = true
                viewModel.resetState()
                Toast.makeText(
                    requireContext(),
                    if (AuthEmailPolicy.isAdmin(state.email)) {
                        "Account created. You can sign in now."
                    } else {
                        AuthEmailPolicy.VERIFICATION_SENT_MESSAGE
                    },
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }
            is AuthState.Error -> {
                binding.createAccountButton.isEnabled = true
                binding.googleRegisterButton.isEnabled = true
                binding.errorText.text = state.message
                binding.errorText.isVisible = true
            }
            is AuthState.Idle -> {
                binding.createAccountButton.isEnabled = true
                binding.googleRegisterButton.isEnabled = true
                binding.errorText.isVisible = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
