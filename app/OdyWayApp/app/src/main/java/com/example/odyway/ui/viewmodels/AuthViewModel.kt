package com.example.odyway.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.odyway.domain.AuthRepository
import com.example.odyway.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Estado del usuario logueado (Automático)
    val currentUser: StateFlow<User?> = authRepository.currentUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    // Estado para gestionar cargas (ruedita girando) en las pantallas
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Estado para mostrar errores (ej. "Contraseña incorrecta")
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            authRepository.login(email, pass).fold(
                onSuccess = { onSuccess() },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    fun register(email: String, pass: String, username: String, fullName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            authRepository.register(email, pass, username, fullName).fold(
                onSuccess = { onSuccess() },
                onFailure = { error -> _errorMessage.value = error.message }
            )

            _isLoading.value = false
        }
    }

    fun logout(onSuccess: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onSuccess()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}