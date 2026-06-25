package com.aks.offvault.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

sealed class AuthState {
    object Locked : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

class LockViewModel : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Locked)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun onAuthenticated() {
        _authState.value = AuthState.Authenticated
    }

    fun onAuthError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun lock() {
        _authState.value = AuthState.Locked
    }
}
