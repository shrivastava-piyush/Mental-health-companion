package com.wellness.companion.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

/**
 * Local-only biometric gate. Renders a soft gradient background and a pulsing
 * fingerprint icon. On success it calls [onUnlocked]; on unsupported devices
 * it exposes a "Continue" button so nobody is locked out of their own data.
 */
@Composable
fun BiometricGateScreen(onUnlocked: () -> Unit) {
    val ctx = LocalContext.current
    val activity = ctx as? FragmentActivity
    val launcher = remember(activity) { activity?.let(::BiometricPromptLauncher) }

    var attempted by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    val unsupported = launcher?.availability() is BiometricPromptLauncher.Outcome.Unsupported

    LaunchedEffect(launcher) {
        if (launcher != null && !unsupported && !attempted) {
            attempted = true
            launcher.prompt { outcome ->
                when (outcome) {
                    is BiometricPromptLauncher.Outcome.Success -> onUnlocked()
                    is BiometricPromptLauncher.Outcome.Cancelled -> message = "Tap to try again"
                    is BiometricPromptLauncher.Outcome.Failure -> message = outcome.message
                    is BiometricPromptLauncher.Outcome.Unsupported -> onUnlocked()
                }
            }
        }
    }

    val scheme = MaterialTheme.colorScheme
    val pulse by animateFloatAsState(
        targetValue = if (attempted) 1.05f else 1f,
        animationSpec = tween(durationMillis = 1400),
        label = "biometric-pulse",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        scheme.primaryContainer.copy(alpha = 0.55f),
                        scheme.background,
                    )
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = null,
                tint = scheme.primary,
                modifier = Modifier
                    .size(112.dp)
                    .scale(pulse),
            )
            Text(
                "Wellness Companion",
                style = MaterialTheme.typography.headlineMedium,
                color = scheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                "Private by design. Unlock with your biometric to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
            message?.let {
                Text(it, style = MaterialTheme.typography.labelMedium, color = scheme.error)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (launcher == null || unsupported) onUnlocked()
                    else launcher.prompt { outcome ->
                        when (outcome) {
                            is BiometricPromptLauncher.Outcome.Success -> onUnlocked()
                            is BiometricPromptLauncher.Outcome.Cancelled -> message = "Cancelled — tap to retry"
                            is BiometricPromptLauncher.Outcome.Failure -> message = outcome.message
                            is BiometricPromptLauncher.Outcome.Unsupported -> onUnlocked()
                        }
                    }
                },
            ) {
                Text(if (unsupported) "Continue" else "Unlock")
            }
        }
    }
}
