package com.aks.offvault.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

class BiometricAuthManager(private val activity: FragmentActivity) {

    fun canAuthenticate(): Boolean {
        val manager = BiometricManager.from(activity)
        val result = manager.canAuthenticate(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
        return result == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticate(
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                // ERROR_USER_CANCELED and ERROR_NEGATIVE_BUTTON mean the user dismissed the
                // prompt intentionally — treat as silent cancel, not a failure to surface.
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON
                ) {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                // Individual scan failed (wrong finger) — the OS already shows feedback,
                // so we only forward this for optional UI feedback.
                onFailed()
            }
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock OffVault")
            .setSubtitle("Use fingerprint or device PIN / pattern / password")
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        BiometricPrompt(activity, executor, callback).authenticate(promptInfo)
    }
}
