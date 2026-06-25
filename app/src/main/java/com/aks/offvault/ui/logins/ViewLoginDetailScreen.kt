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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.theme.SectionPurple
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
                    Text(
                        text = login?.title ?: "Login",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        login?.let { l ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(SectionPurple.copy(alpha = 0.12f))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Outlined.VpnKey,
                            contentDescription = null,
                            tint = SectionPurple,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = l.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Details card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(vertical = 4.dp)
                ) {
                    // Username — always visible, always copyable
                    CopyableRow(
                        label = "Username",
                        value = l.username
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    // Password — hidden until revealed; copyable at all times
                    PasswordRow(
                        password = l.password,
                        isVisible = passwordVisible,
                        onToggleVisibility = { passwordVisible = !passwordVisible }
                    )

                    if (l.info.isNotBlank()) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        PlainRow(label = "Info", value = l.info)
                    }
                }
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Login not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CopyableRow(label: String, value: String) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var justCopied by remember { mutableStateOf(false) }

    LaunchedEffect(justCopied) {
        if (justCopied) {
            delay(2_000L)
            justCopied = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.30f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.55f),
            textAlign = TextAlign.Start
        )
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(value))
                justCopied = true
                scope.launch {
                    delay(30_000L)
                    clipboardManager.setText(AnnotatedString(""))
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (justCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
                contentDescription = if (justCopied) "Copied" else "Copy",
                modifier = Modifier.size(18.dp),
                tint = if (justCopied) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PasswordRow(
    password: String,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    var justCopied by remember { mutableStateOf(false) }

    LaunchedEffect(justCopied) {
        if (justCopied) {
            delay(2_000L)
            justCopied = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Password",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.30f)
        )
        Text(
            text = if (isVisible) password else "•".repeat(password.length.coerceAtMost(12)),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            letterSpacing = if (isVisible) 0.5.sp else 2.sp,
            modifier = Modifier.weight(0.40f),
            textAlign = TextAlign.Start
        )
        // Eye toggle
        IconButton(
            onClick = onToggleVisibility,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (isVisible) "Hide password" else "Show password",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        // Copy button
        IconButton(
            onClick = {
                clipboardManager.setText(AnnotatedString(password))
                justCopied = true
                scope.launch {
                    delay(30_000L)
                    clipboardManager.setText(AnnotatedString(""))
                }
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (justCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
                contentDescription = if (justCopied) "Copied" else "Copy password",
                modifier = Modifier.size(18.dp),
                tint = if (justCopied) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun PlainRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.30f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.70f),
            textAlign = TextAlign.Start
        )
    }
}