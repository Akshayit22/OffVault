package com.aks.offvault.ui.section

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.components.EmptyState
import com.aks.offvault.ui.components.VaultFab
import com.aks.offvault.ui.home.SectionItem
import com.aks.offvault.ui.theme.VaultBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SectionListScreen(
    section: SectionItem,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = section.label, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        floatingActionButton = {
            VaultFab(
                onClick = { /* Add item — wired up in the data layer feature */ },
                containerColor = section.accentColor,
                contentDescription = "Add ${section.label}",
                icon = Icons.Filled.Add
            )
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyState(
                icon = section.icon,
                accentColor = section.accentColor,
                title = "No ${section.label} saved yet",
                subtitle = "Tap + to add your first entry"
            )
        }
    }
}
