package com.aks.offvault.ui.theme

import androidx.compose.ui.graphics.Color

// ── OffVault OLED dark design system ───────────────────────────────────────
// True near-black scaffold background — real OLED black savings, high contrast.
val VaultBackground = Color(0xFF04060B)

// Elevated surface used for cards / sheets / dialogs.
val VaultSurface = Color(0xFF0D1119)
val VaultSurfaceBorder = Color(0x18949FB8) // rgba(148,163,184,0.09)

// Primary accent (blue).
val VaultPrimary = Color(0xFF3B82F6)
val VaultPrimaryHover = Color(0xFF2563EB)
val VaultPrimarySoftFill = Color(0x243B82F6) // rgba(59,130,246,0.14)

// Text.
val VaultTextPrimary = Color(0xFFF1F5F9)
val VaultTextSecondary = Color(0xFF94A3B8)
val VaultTextMuted = Color(0xFF647085)

// Status.
val VaultDanger = Color(0xFFF87171)
val VaultSuccess = Color(0xFF34D399)

// ── Section / category accent colours ──────────────────────────────────────
val SectionBlue = Color(0xFF60A5FA)    // Cards
val SectionTeal = Color(0xFF34D399)    // Documents
val SectionPurple = Color(0xFFA78BFA)  // Logins
val SectionPurpleDeep = Color(0xFF8B5CF6)
val SectionOrange = Color(0xFFFBBF24)  // Others

// ── Password strength scale ─────────────────────────────────────────────────
val StrengthVeryWeak = Color(0xFFF43F5E)
val StrengthWeak = Color(0xFFFB923C)
val StrengthFair = Color(0xFFF59E0B)
val StrengthGood = Color(0xFF34D399)
val StrengthStrong = Color(0xFF22C55E)
