package com.aks.offvault.ui.others

import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.aks.offvault.data.model.Other
import com.aks.offvault.ui.components.VaultPrimaryButton
import com.aks.offvault.ui.theme.SectionOrange
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditOtherScreen(
    viewModel: OtherViewModel,
    editOtherId: Long? = null,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val isEditMode = editOtherId != null
    val existingOther by if (isEditMode) {
        viewModel.getOtherFlow(editOtherId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var title by rememberSaveable { mutableStateOf("") }
    var info by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(existingOther) {
        existingOther?.let { other ->
            title = other.title
            info = other.info
        }
    }

    val isFormValid = title.isNotBlank()

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SectionOrange,
        unfocusedBorderColor = VaultSurfaceBorder,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = SectionOrange
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Entry" else "Add Entry",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(VaultBackground)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                VaultPrimaryButton(
                    text = if (isEditMode) "Save Changes" else "Add Entry",
                    enabled = isFormValid,
                    containerColor = SectionOrange,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val other = Other(
                            id = if (isEditMode) existingOther!!.id else 0,
                            title = title.trim(),
                            info = info.trim(),
                            createdAt = if (isEditMode) existingOther!!.createdAt else System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        if (isEditMode) viewModel.updateOther(other) else viewModel.insertOther(other)
                        onSaved()
                    }
                )
            }
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FieldLabel("Title *")
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = { Text("e.g. Bank Account, WiFi Password") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Info")
            OutlinedTextField(
                value = info,
                onValueChange = { info = it },
                placeholder = { Text("Details (optional)") },
                minLines = 4,
                maxLines = 10,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(88.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = VaultTextSecondary,
        fontWeight = FontWeight.Medium
    )
}
