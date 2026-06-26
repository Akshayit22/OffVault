package com.aks.offvault.ui.data

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material.icons.outlined.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataScreen(viewModel: DataViewModel, onBackClick: () -> Unit) {
    val stats by viewModel.stats.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Dialog state ──────────────────────────────────────────────────────────
    var showExportDialog by remember { mutableStateOf(false) }
    var showImportPassphraseDialog by remember { mutableStateOf(false) }
    var showImportModeDialog by remember { mutableStateOf(false) }
    var pendingExportPassphrase by remember { mutableStateOf("") }
    var pendingImportUri by remember { mutableStateOf<Uri?>(null) }
    var pendingImportPassphrase by remember { mutableStateOf("") }

    // ── File pickers ──────────────────────────────────────────────────────────
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { viewModel.exportBackup(pendingExportPassphrase, it) }
        pendingExportPassphrase = ""
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let {
            pendingImportUri = it
            showImportPassphraseDialog = true
        }
    }

    // ── Snackbar on success/error ─────────────────────────────────────────────
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is DataUiState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearState()
            }
            is DataUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearState()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Data") },
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // ── Stats ─────────────────────────────────────────────────────────
            Text(
                text = "Vault Summary",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.CreditCard,
                    label = "Cards",
                    count = stats.cardCount
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Description,
                    label = "Documents",
                    count = stats.documentCount
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.VpnKey,
                    label = "Logins",
                    count = stats.loginCount
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Outlined.Category,
                    label = "Others",
                    count = stats.otherCount
                )
            }

            // ── Backup & Restore ──────────────────────────────────────────────
            Text(
                text = "Backup & Restore",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Export Backup",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Save an encrypted backup of all your vault data to a file.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { showExportDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.Upload,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Export Backup")
                    }
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Import Backup",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Restore your vault data from a previously exported backup file.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Outlined.Download,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Text("  Import Backup")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ── Export passphrase dialog ──────────────────────────────────────────────
    if (showExportDialog) {
        ExportPassphraseDialog(
            onConfirm = { passphrase ->
                pendingExportPassphrase = passphrase
                showExportDialog = false
                exportLauncher.launch("offvault_backup.json")
            },
            onDismiss = { showExportDialog = false }
        )
    }

    // ── Import passphrase dialog ──────────────────────────────────────────────
    if (showImportPassphraseDialog) {
        ImportPassphraseDialog(
            onConfirm = { passphrase ->
                pendingImportPassphrase = passphrase
                showImportPassphraseDialog = false
                showImportModeDialog = true
            },
            onDismiss = {
                showImportPassphraseDialog = false
                pendingImportUri = null
            }
        )
    }

    // ── Import mode dialog (Replace / Merge) ──────────────────────────────────
    if (showImportModeDialog) {
        AlertDialog(
            onDismissRequest = {
                showImportModeDialog = false
                pendingImportUri = null
                pendingImportPassphrase = ""
            },
            title = { Text("Restore Mode") },
            text = { Text("Replace all existing data with the backup, or merge the backup into your current data?") },
            confirmButton = {
                Button(onClick = {
                    showImportModeDialog = false
                    pendingImportUri?.let {
                        viewModel.importBackup(pendingImportPassphrase, it, replaceExisting = true)
                    }
                    pendingImportPassphrase = ""
                    pendingImportUri = null
                }) { Text("Replace") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImportModeDialog = false
                    pendingImportUri?.let {
                        viewModel.importBackup(pendingImportPassphrase, it, replaceExisting = false)
                    }
                    pendingImportPassphrase = ""
                    pendingImportUri = null
                }) { Text("Merge") }
            }
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    label: String,
    count: Int
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ExportPassphraseDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var passphrase by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    val mismatch = passphrase.isNotEmpty() && confirm.isNotEmpty() && passphrase != confirm
    val valid = passphrase.length >= 6 && passphrase == confirm

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Backup Passphrase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Choose a passphrase to encrypt your backup. You will need it to restore.",
                    fontSize = 14.sp
                )
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text("Passphrase") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = { Text("Confirm passphrase") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = mismatch,
                    supportingText = if (mismatch) {
                        { Text("Passphrases do not match") }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(passphrase) }, enabled = valid) { Text("Export") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ImportPassphraseDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var passphrase by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enter Passphrase") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Enter the passphrase used when this backup was created.", fontSize = 14.sp)
                OutlinedTextField(
                    value = passphrase,
                    onValueChange = { passphrase = it },
                    label = { Text("Passphrase") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(passphrase) }, enabled = passphrase.isNotEmpty()) {
                Text("Continue")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
