package com.aks.offvault.ui.home

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class SectionItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val description: String,
    val accentColor: Color
)
