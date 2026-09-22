package com.findit.app.presentation.auth

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.findit.app.data.repository.AuthRepository
import com.findit.app.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // Observe the current user from the repository
    val currentUser: StateFlow<User?> = repository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState = _authState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signIn(email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(name: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.signUp(name, email, password)
            result.onSuccess {
                _authState.value = AuthState.Success
            }.onFailure { e ->
                _authState.value = AuthState.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            repository.signOut()
            _authState.value = AuthState.Idle
        }
    }

    fun updateProfilePhoto(
        uri: Uri,
        contentResolver: ContentResolver,
        onResult: (Result<String>) -> Unit
    ) {
        viewModelScope.launch {
            onResult(repository.updateProfilePhoto(uri, contentResolver))
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}