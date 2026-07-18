package com.mavi.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mavi.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun register(username: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        val result = authRepository.register(username, password)
        _uiState.value = if (result.isSuccess) {
            AuthUiState.Success("Registration successful")
        } else {
            AuthUiState.Error(result.exceptionOrNull()?.message ?: "Registration failed")
        }
    }

    fun login(username: String, password: String) = viewModelScope.launch {
        _uiState.value = AuthUiState.Loading
        val result = authRepository.login(username, password)
        _uiState.value = if (result.isSuccess) {
            AuthUiState.Success("Login successful")
        } else {
            AuthUiState.Error(result.exceptionOrNull()?.message ?: "Login failed")
        }
    }

    fun logout() = viewModelScope.launch {
        authRepository.logout()
        _uiState.value = AuthUiState.Idle
    }
}

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
