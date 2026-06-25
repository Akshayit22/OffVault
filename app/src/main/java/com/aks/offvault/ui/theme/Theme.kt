package com.aks.offvault.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = VaultAccentBlue,
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004D6F),
    onPrimaryContainer = Color(0xFFBDE9FF),
    background = VaultNavyDark,
    onBackground = Color.White,
    surface = VaultNavyMedium,
    onSurface = Color.White,
    surfaceVariant = VaultNavySurface,
    onSurfaceVariant = VaultSubtitle,
    outline = VaultHint
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006494),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBDE9FF),
    onPrimaryContainer = Color(0xFF001E2C),
    background = Color(0xFFF6FAFE),
    onBackground = Color(0xFF191C1E),
    surface = Color(0xFFF6FAFE),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDCE4EC),
    onSurfaceVariant = Color(0xFF40484F),
    outline = Color(0xFF70787F)
)

@Composable
fun OffVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color deliberately disabled — vault requires a consistent secure aesthetic
    // regardless of device wallpaper or system accent colour.
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
