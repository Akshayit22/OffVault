package com.aks.offvault.ui.logins

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
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aks.offvault.data.model.LoginDetail
import com.aks.offvault.ui.components.StrengthMeter
import com.aks.offvault.ui.components.VaultPrimaryButton
import com.aks.offvault.ui.components.calculatePasswordStrength
import com.aks.offvault.ui.theme.SectionPurple
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditLoginDetailScreen(
    viewModel: LoginDetailViewModel,
    editLoginDetailId: Long? = null,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val isEditMode = editLoginDetailId != null
    val existingLogin by if (isEditMode) {
        viewModel.getLoginDetailFlow(editLoginDetailId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var title by rememberSaveable { mutableStateOf("") }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var info by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existingLogin) {
        existingLogin?.let { login ->
            title = login.title
            username = login.username
            password = login.password
            info = login.info
        }
    }

    val isFormValid = title.isNotBlank() && username.isNotBlank() && password.isNotBlank()
    val strength = calculatePasswordStrength(password)

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SectionPurple,
        unfocusedBorderColor = VaultSurfaceBorder,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = SectionPurple
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Login" else "Add Login",
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
                    text = if (isEditMode) "Save Changes" else "Add Login",
                    enabled = isFormValid,
                    containerColor = SectionPurple,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val login = LoginDetail(
                            id = if (isEditMode) existingLogin!!.id else 0,
                            title = title.trim(),
                            username = username.trim(),
                            password = password,
                            info = info.trim(),
                            createdAt = if (isEditMode) existingLogin!!.createdAt else System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        if (isEditMode) viewModel.updateLoginDetail(login) else viewModel.insertLoginDetail(login)
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
                placeholder = { Text("e.g. Gmail, Instagram, Netflix") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Username *")
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = { Text("Username or email") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            FieldLabel("Password *")
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = VaultTextSecondary
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // Live strength meter — updates as the user types
            if (password.isNotEmpty()) {
                StrengthMeter(strength = strength, modifier = Modifier.padding(horizontal = 2.dp))
            }

            FieldLabel("Info")
            OutlinedTextField(
                value = info,
                onValueChange = { info = it },
                placeholder = { Text("Additional details (optional)") },
                minLines = 3,
                maxLines = 6,
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
