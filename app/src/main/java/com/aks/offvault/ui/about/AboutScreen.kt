package com.aks.offvault.ui.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.components.IconTile
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultPrimary
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconTile(
                icon = Icons.Outlined.Shield,
                tint = VaultPrimary,
                size = 84.dp,
                iconSize = 40.dp,
                cornerRadius = 22.dp,
                fillAlpha = 0.14f,
                modifier = Modifier.padding(top = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "OffVault",
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Version 1.0",
                fontSize = 14.sp,
                color = VaultTextSecondary
            )

            Spacer(modifier = Modifier.height(28.dp))

            VaultCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    AboutRow(label = "App", value = "OffVault")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Version", value = "1.0")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Platform", value = "Android")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Creator", value = "Akshay Telang")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            VaultCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "About OffVault",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "OffVault is a fully offline, secure personal vault for Android. " +
                                "It lets you safely store cards, documents, login details, and " +
                                "other sensitive information — entirely on your device, with no " +
                                "network access and no third-party accounts.\n\n" +
                                "Your data never leaves your phone.",
                        fontSize = 14.sp,
                        color = VaultTextSecondary,
                        lineHeight = 21.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            VaultCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = VaultPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Security & Encryption",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    AboutRow(label = "Database", value = "SQLCipher AES-256")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Key storage", value = "Android Keystore")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Backup cipher", value = "AES-256-GCM")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Key derivation", value = "PBKDF2 · 310k rounds")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Authentication", value = "Biometric / Device PIN")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = VaultSurfaceBorder)
                    AboutRow(label = "Network access", value = "None")
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Made with care for your privacy.",
                fontSize = 13.sp,
                color = VaultTextSecondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun AboutRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = VaultTextSecondary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
