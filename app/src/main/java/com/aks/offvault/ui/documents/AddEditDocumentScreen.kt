package com.aks.offvault.ui.documents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.aks.offvault.data.model.Document
import com.aks.offvault.ui.theme.SectionTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDocumentScreen(
    viewModel: DocumentViewModel,
    editDocumentId: Long? = null,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val isEditMode = editDocumentId != null
    val existingDocument by if (isEditMode) {
        viewModel.getDocumentFlow(editDocumentId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var title by rememberSaveable { mutableStateOf("") }
    var documentId by rememberSaveable { mutableStateOf("") }
    var info by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(existingDocument) {
        existingDocument?.let { doc ->
            title = doc.title
            documentId = doc.documentId
            info = doc.info
        }
    }

    val isFormValid = title.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Document" else "Add Document",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FieldLabel("Title *")
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Aadhaar Card, PAN Card, Passport") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("ID / Document Number")
            OutlinedTextField(
                value = documentId,
                onValueChange = { documentId = it },
                placeholder = { Text("Alphanumeric ID") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Info")
            OutlinedTextField(
                value = info,
                onValueChange = { info = it },
                placeholder = { Text("Additional details (optional)") },
                minLines = 3,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val document = Document(
                        id = if (isEditMode) existingDocument!!.id else 0,
                        title = title.trim(),
                        documentId = documentId.trim(),
                        info = info.trim(),
                        createdAt = if (isEditMode) existingDocument!!.createdAt else System.currentTimeMillis(),
                        updatedAt = System.currentTimeMillis()
                    )
                    if (isEditMode) viewModel.updateDocument(document) else viewModel.insertDocument(document)
                    onSaved()
                },
                enabled = isFormValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SectionTeal)
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Add Document",
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium
    )
}