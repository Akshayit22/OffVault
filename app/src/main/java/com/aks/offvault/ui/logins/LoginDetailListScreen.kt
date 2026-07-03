package com.aks.offvault.ui.logins

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.VpnKey
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.data.model.LoginDetail
import com.aks.offvault.ui.components.EmptyState
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.VaultFab
import com.aks.offvault.ui.components.VaultSearchField
import com.aks.offvault.ui.components.calculatePasswordStrength
import com.aks.offvault.ui.theme.SectionPurple
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginDetailListScreen(
    viewModel: LoginDetailViewModel,
    onLoginDetailClick: (LoginDetail) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val loginDetails by viewModel.loginDetails.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredLogins = if (searchQuery.isBlank()) loginDetails else {
        val q = searchQuery.trim().lowercase()
        loginDetails.filter { login ->
            login.title.lowercase().contains(q) ||
            login.username.lowercase().contains(q) ||
            login.info.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Logins", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        floatingActionButton = {
            VaultFab(onClick = onAddClick, containerColor = SectionPurple, contentDescription = "Add login")
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            VaultSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search logins…",
                accentColor = SectionPurple,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when {
                loginDetails.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Outlined.VpnKey,
                        accentColor = SectionPurple,
                        title = "No logins saved yet",
                        subtitle = "Tap + to add your first login"
                    )
                }
                filteredLogins.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No logins match \"$searchQuery\"", color = VaultTextSecondary)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = filteredLogins, key = { it.id }) { login ->
                        LoginDetailListItem(login = login, onClick = { onLoginDetailClick(login) })
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginDetailListItem(login: LoginDetail, onClick: () -> Unit) {
    val strength = calculatePasswordStrength(login.password)
    val initial = login.title.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "?"

    VaultCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(SectionPurple.copy(alpha = 0.16f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = SectionPurple
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = login.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (login.username.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = login.username,
                        fontSize = 13.sp,
                        color = VaultTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(strength.color, CircleShape)
            )

            Spacer(Modifier.width(12.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
                contentDescription = null,
                tint = VaultTextSecondary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
