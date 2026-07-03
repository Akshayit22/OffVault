package com.aks.offvault.ui.lock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.auth.AuthState
import com.aks.offvault.auth.LockViewModel
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultPrimary
import com.aks.offvault.ui.theme.VaultPrimaryHover
import com.aks.offvault.ui.theme.VaultTextMuted
import com.aks.offvault.ui.theme.VaultTextSecondary

@Composable
fun LockScreen(
    viewModel: LockViewModel,
    onUnlockRequest: () -> Unit
) {
    val authState by viewModel.authState.collectAsState()

    // Auto-trigger biometric prompt when this screen first enters composition
    LaunchedEffect(Unit) {
        onUnlockRequest()
    }

    var buttonPressed by remember { mutableStateOf(false) }
    val buttonScale by animateFloatAsState(
        targetValue = if (buttonPressed) 0.92f else 1f,
        animationSpec = tween(100),
        finishedListener = { buttonPressed = false },
        label = "fingerprintButtonScale"
    )

    // Subtle pulsing glow behind the fingerprint button
    val infiniteTransition = rememberInfiniteTransition(label = "fingerprintGlow")
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowScale"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VaultBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            // Shield logo tile with blue gradient
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(colors = listOf(VaultPrimary, VaultPrimaryHover))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "OffVault",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your data, offline & encrypted",
                color = VaultTextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(72.dp))

            // Pulsing glow + fingerprint button
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .scale(glowScale)
                        .clip(CircleShape)
                        .background(VaultPrimary.copy(alpha = glowAlpha))
                )
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .scale(buttonScale)
                        .clip(CircleShape)
                        .background(VaultPrimary.copy(alpha = 0.16f))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            buttonPressed = true
                            onUnlockRequest()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Fingerprint,
                        contentDescription = "Unlock with fingerprint",
                        tint = VaultPrimary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tap to unlock",
                color = VaultTextMuted,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Use PIN instead",
                color = VaultPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onUnlockRequest
                )
            )

            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
