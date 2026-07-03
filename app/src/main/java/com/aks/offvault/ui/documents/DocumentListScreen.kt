package com.aks.offvault.ui.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.data.model.Document
import com.aks.offvault.ui.components.IconTile
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.VaultFab
import com.aks.offvault.ui.components.VaultPrimaryButton
import com.aks.offvault.ui.components.VaultSearchField
import com.aks.offvault.ui.theme.SectionTeal
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    viewModel: DocumentViewModel,
    onDocumentClick: (Document) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val documents by viewModel.documents.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredDocuments = if (searchQuery.isBlank()) documents else {
        val q = searchQuery.trim().lowercase()
        documents.filter { doc ->
            doc.title.lowercase().contains(q) ||
            doc.documentId.lowercase().contains(q) ||
            doc.info.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Documents", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        floatingActionButton = {
            VaultFab(onClick = onAddClick, containerColor = SectionTeal, contentDescription = "Add document")
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            VaultSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search documents…",
                accentColor = SectionTeal,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when {
                documents.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    DocumentsEmptyState(onAddClick = onAddClick)
                }
                filteredDocuments.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No documents match \"$searchQuery\"", color = VaultTextSecondary)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = filteredDocuments, key = { it.id }) { document ->
                        DocumentListItem(document = document, onClick = { onDocumentClick(document) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsEmptyState(onAddClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(32.dp)
    ) {
        IconTile(
            icon = Icons.Outlined.Description,
            tint = SectionTeal,
            size = 96.dp,
            iconSize = 46.dp,
            cornerRadius = 26.dp,
            fillAlpha = 0.1f
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "No documents yet",
            fontWeight = FontWeight.SemiBold,
            fontSize = 19.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            "Store IDs, passports, licences and more — all encrypted and offline.",
            fontSize = 14.sp,
            color = VaultTextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        VaultPrimaryButton(
            text = "Add Document",
            onClick = onAddClick,
            containerColor = SectionTeal,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Composable
private fun DocumentListItem(document: Document, onClick: () -> Unit) {
    VaultCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = Icons.Outlined.Description, tint = SectionTeal, size = 48.dp, iconSize = 24.dp)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (document.documentId.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = document.documentId,
                        fontSize = 13.sp,
                        color = VaultTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
