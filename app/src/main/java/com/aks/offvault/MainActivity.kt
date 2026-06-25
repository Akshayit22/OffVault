package com.aks.offvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.aks.offvault.auth.AuthState
import com.aks.offvault.auth.BiometricAuthManager
import com.aks.offvault.auth.LockViewModel
import com.aks.offvault.ui.home.HomeScreen
import com.aks.offvault.ui.home.HomeViewModel
import com.aks.offvault.ui.home.SectionItem
import com.aks.offvault.ui.lock.LockScreen
import com.aks.offvault.ui.theme.OffVaultTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

class MainActivity : FragmentActivity() {

    private val lockViewModel: LockViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
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
                        HomeScreen(
                            viewModel = homeViewModel,
                            onSectionClick = { _: SectionItem ->
                                // Navigation to section detail screens — coming in next feature
                            },
                            onLockClick = { lockViewModel.lock() }
                        )
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
        // Re-lock the vault whenever the app loses focus
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