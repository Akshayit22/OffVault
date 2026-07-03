package com.aks.offvault.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// OffVault ships a single, deliberate OLED-dark aesthetic — a secure vault should look
// and feel consistent regardless of the device's system theme or wallpaper.
private val VaultDarkColorScheme = darkColorScheme(
    primary = VaultPrimary,
    onPrimary = Color.White,
    primaryContainer = VaultPrimarySoftFill,
    onPrimaryContainer = VaultPrimary,
    background = VaultBackground,
    onBackground = VaultTextPrimary,
    surface = VaultSurface,
    onSurface = VaultTextPrimary,
    surfaceVariant = VaultSurface,
    onSurfaceVariant = VaultTextSecondary,
    outline = VaultSurfaceBorder,
    outlineVariant = VaultSurfaceBorder,
    error = VaultDanger,
    onError = Color.White
)

@Composable
fun OffVaultTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // Dynamic color and light mode are deliberately disabled — the vault always renders
    // in the same OLED-dark design system regardless of device theme.
    MaterialTheme(
        colorScheme = VaultDarkColorScheme,
        typography = Typography,
        content = content
    )
}
