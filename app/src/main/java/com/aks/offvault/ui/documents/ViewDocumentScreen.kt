package com.aks.offvault.ui.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.components.CopyableDetailRow
import com.aks.offvault.ui.components.IconTile
import com.aks.offvault.ui.components.PlainDetailRow
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.copyWithAutoClear
import com.aks.offvault.ui.theme.SectionTeal
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewDocumentScreen(
    viewModel: DocumentViewModel,
    documentId: Long,
    onEditClick: () -> Unit,
    onDeleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val document by viewModel.getDocumentFlow(documentId).collectAsState(initial = null)
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Document") },
            text = { Text("This document will be permanently deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    document?.let { viewModel.deleteDocument(it) }
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
                title = { Text(text = document?.title ?: "Document", fontWeight = FontWeight.Bold, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit document")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete document",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        document?.let { doc ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                VaultCard {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconTile(icon = Icons.Outlined.Description, tint = SectionTeal, size = 56.dp, iconSize = 28.dp)
                        Spacer(Modifier.width(16.dp))
                        Text(
                            text = doc.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                VaultCard {
                    CopyableDetailRow(
                        label = "ID / Number",
                        value = doc.documentId.ifBlank { "—" },
                        onCopy = if (doc.documentId.isNotBlank()) {
                            { copyWithAutoClear(clipboardManager, scope, doc.documentId) }
                        } else null
                    )

                    if (doc.info.isNotBlank()) {
                        HorizontalDivider(color = VaultSurfaceBorder)
                        PlainDetailRow(label = "Info", value = doc.info)
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Document not found", color = VaultTextSecondary)
        }
    }
}
