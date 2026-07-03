package com.aks.offvault.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// OffVault's design system calls for Manrope (UI text) and JetBrains Mono (card numbers,
// CVV, passwords). No offline-safe font binaries are bundled in this build — the app must
// ship with zero network access, so no runtime/downloadable font provider is used — and the
// closest system-installed equivalents stand in: a geometric sans for UI chrome and a true
// monospace face for masked/sensitive numeric and alphanumeric values.
val VaultUiFontFamily = FontFamily.SansSerif
val VaultMonoFontFamily = FontFamily.Monospace

val Typography = Typography(
    displayLarge = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Bold, fontSize = 40.sp, lineHeight = 46.sp, letterSpacing = 0.sp),
    headlineLarge = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Bold, fontSize = 28.sp, lineHeight = 34.sp),
    headlineMedium = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 22.sp),
    titleSmall = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.2.sp),
    bodyMedium = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.2.sp),
    bodySmall = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp, lineHeight = 18.sp),
    labelLarge = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.3.sp),
    labelSmall = TextStyle(fontFamily = VaultUiFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp)
)
