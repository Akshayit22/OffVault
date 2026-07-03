package com.aks.offvault.ui.logins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.components.CopyableDetailRow
import com.aks.offvault.ui.components.IconTile
import com.aks.offvault.ui.components.PlainDetailRow
import com.aks.offvault.ui.components.SensitiveDetailRow
import com.aks.offvault.ui.components.StrengthMeter
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.calculatePasswordStrength
import com.aks.offvault.ui.components.copyWithAutoClear
import com.aks.offvault.ui.theme.SectionPurple
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewLoginDetailScreen(
    viewModel: LoginDetailViewModel,
    loginDetailId: Long,
    onEditClick: () -> Unit,
    onDeleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val login by viewModel.getLoginDetailFlow(loginDetailId).collectAsState(initial = null)
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Login") },
            text = { Text("This login will be permanently deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    login?.let { viewModel.deleteLoginDetail(it) }
                    showDeleteDialog = false
                    onDeleted()
                }) { Text("Delete", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = login?.title ?: "Login", fontWeight = FontWeight.Bold, maxLines = 1)
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit login")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete login",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        login?.let { l ->
            val strength = calculatePasswordStrength(l.password)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header card with key icon tile + title
                VaultCard {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(icon = Icons.Outlined.VpnKey, tint = SectionPurple, size = 56.dp, iconSize = 28.dp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = l.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Details card
                VaultCard {
                    CopyableDetailRow(
                        label = "Username",
                        value = l.username,
                        onCopy = { copyWithAutoClear(clipboardManager, scope, l.username) }
                    )

                    HorizontalDivider(color = VaultSurfaceBorder)

                    SensitiveDetailRow(
                        label = "Password",
                        visibleValue = l.password,
                        isVisible = passwordVisible,
                        onToggle = { passwordVisible = !passwordVisible },
                        onCopy = { copyWithAutoClear(clipboardManager, scope, l.password) }
                    )

                    // Strength meter
                    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Spacer(Modifier.height(2.dp))
                        StrengthMeter(strength = strength)
                        Spacer(Modifier.height(10.dp))
                    }

                    if (l.info.isNotBlank()) {
                        HorizontalDivider(color = VaultSurfaceBorder)
                        PlainDetailRow(label = "Info", value = l.info)
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Login not found", color = VaultTextSecondary)
        }
    }
}
