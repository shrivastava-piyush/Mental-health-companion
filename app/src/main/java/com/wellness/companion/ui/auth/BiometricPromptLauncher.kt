package com.wellness.companion.ui.auth

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Thin wrapper around androidx.biometric so the composable layer stays free of
 * Android-framework imports. Callback-based API; callers convert to Flow if
 * desired.
 */
class BiometricPromptLauncher(private val activity: FragmentActivity) {

    sealed interface Outcome {
        data object Success : Outcome
        data class Failure(val code: Int, val message: String) : Outcome
        data object Cancelled : Outcome
        data object Unsupported : Outcome
    }

    fun availability(): Outcome {
        val manager = BiometricManager.from(activity)
        val caps = manager.canAuthenticate(ALLOWED)
        return if (caps == BiometricManager.BIOMETRIC_SUCCESS) Outcome.Success else Outcome.Unsupported
    }

    fun prompt(onResult: (Outcome) -> Unit) {
        if (availability() is Outcome.Unsupported) {
            // Graceful fallback so devices without biometrics aren't locked out.
            onResult(Outcome.Unsupported)
            return
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onResult(Outcome.Success)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onResult(
                        if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                            errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON
                        ) Outcome.Cancelled
                        else Outcome.Failure(errorCode, errString.toString())
                    )
                }
            },
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock your journal")
            .setSubtitle("Your entries stay on this device.")
            .setAllowedAuthenticators(ALLOWED)
            .setNegativeButtonText("Cancel")
            .build()

        prompt.authenticate(info)
    }

    private companion object {
        const val ALLOWED =
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.BIOMETRIC_WEAK
    }
}
