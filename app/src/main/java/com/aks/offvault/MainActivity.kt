package com.aks.offvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.aks.offvault.auth.AuthState
import com.aks.offvault.auth.BiometricAuthManager
import com.aks.offvault.auth.LockViewModel
import com.aks.offvault.ui.lock.LockScreen
import com.aks.offvault.ui.theme.OffVaultTheme

class MainActivity : FragmentActivity() {

    private val lockViewModel: LockViewModel by viewModels()
    private lateinit var biometricAuthManager: BiometricAuthManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Prevent screenshots, screen recording, and app-switcher preview leaking vault content
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)

        biometricAuthManager = BiometricAuthManager(this)

        enableEdgeToEdge()
        setContent {
            OffVaultTheme {
                val authState by lockViewModel.authState.collectAsState()

                when (authState) {
                    is AuthState.Authenticated -> {
                        // Phase 1 placeholder — Home screen will replace this
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Welcome to OffVault",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                        }
                    }

                    else -> {
                        LockScreen(
                            viewModel = lockViewModel,
                            onUnlockRequest = ::triggerAuth
                        )
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Re-lock the vault whenever the app loses focus (home button, notification shade, etc.)
        lockViewModel.lock()
    }

    private fun triggerAuth() {
        biometricAuthManager.authenticate(
            onSuccess = { lockViewModel.onAuthenticated() },
            onError = { message -> lockViewModel.onAuthError(message) },
            onFailed = { /* OS already shows per-attempt feedback in the biometric dialog */ }
        )
    }
}
