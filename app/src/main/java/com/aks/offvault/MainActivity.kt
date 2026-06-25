package com.aks.offvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.aks.offvault.auth.AuthState
import com.aks.offvault.auth.BiometricAuthManager
import com.aks.offvault.auth.LockViewModel
import com.aks.offvault.navigation.VaultNavGraph
import com.aks.offvault.ui.home.HomeViewModel
import com.aks.offvault.ui.lock.LockScreen
import com.aks.offvault.ui.theme.OffVaultTheme

class MainActivity : FragmentActivity() {

    private val lockViewModel: LockViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var biometricAuthManager: BiometricAuthManager

    // Timestamp of when the app was last sent to background; 0 means not yet backgrounded.
    private var backgroundedAt: Long = 0L

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
                        VaultNavGraph(
                            homeViewModel = homeViewModel,
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

    override fun onStop() {
        super.onStop()
        backgroundedAt = System.currentTimeMillis()
    }

    override fun onStart() {
        super.onStart()
        // Lock only if the app was in the background for longer than the timeout.
        // backgroundedAt == 0 means first launch — LockViewModel already starts locked.
        if (backgroundedAt > 0L &&
            System.currentTimeMillis() - backgroundedAt >= LOCK_TIMEOUT_MS
        ) {
            lockViewModel.lock()
        }
        backgroundedAt = 0L
    }

    private fun triggerAuth() {
        biometricAuthManager.authenticate(
            onSuccess = { lockViewModel.onAuthenticated() },
            onError = { message -> lockViewModel.onAuthError(message) },
            onFailed = { /* OS already shows per-attempt feedback in the biometric dialog */ }
        )
    }

    companion object {
        private const val LOCK_TIMEOUT_MS = 30_000L
    }
}