package com.aks.offvault.ui.cards

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CreditCard
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
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.CardType
import com.aks.offvault.ui.components.EmptyState
import com.aks.offvault.ui.components.IconTile
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.VaultFab
import com.aks.offvault.ui.components.VaultSearchField
import com.aks.offvault.ui.theme.SectionBlue
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultMonoFontFamily
import com.aks.offvault.ui.theme.VaultTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardListScreen(
    viewModel: CardViewModel,
    onCardClick: (Card) -> Unit,
    onAddClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val cards by viewModel.cards.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val filteredCards = if (searchQuery.isBlank()) cards else {
        val q = searchQuery.trim().lowercase()
        cards.filter { card ->
            card.label.lowercase().contains(q) ||
            card.bankName.lowercase().contains(q) ||
            card.cardNumber.contains(q) ||
            card.notes.lowercase().contains(q)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cards", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        floatingActionButton = {
            VaultFab(onClick = onAddClick, containerColor = SectionBlue, contentDescription = "Add card")
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            VaultSearchField(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                placeholder = "Search cards…",
                accentColor = SectionBlue,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )

            when {
                cards.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = Icons.Outlined.CreditCard,
                        accentColor = SectionBlue,
                        title = "No cards saved yet",
                        subtitle = "Tap + to add your first card"
                    )
                }
                filteredCards.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No cards match \"$searchQuery\"", color = VaultTextSecondary)
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = filteredCards, key = { it.id }) { card ->
                        CardListItem(card = card, onClick = { onCardClick(card) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CardListItem(card: Card, onClick: () -> Unit) {
    val maskedNumber = "•••• •••• •••• ${card.cardNumber.takeLast(4)}"

    VaultCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = Icons.Outlined.CreditCard, tint = SectionBlue, size = 48.dp, iconSize = 24.dp)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = card.label.ifBlank { card.bankName },
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = maskedNumber,
                    fontFamily = VaultMonoFontFamily,
                    fontSize = 13.sp,
                    color = VaultTextSecondary,
                    letterSpacing = 1.sp
                )
                if (card.bankName.isNotBlank()) {
                    Text(
                        text = card.bankName,
                        fontSize = 12.sp,
                        color = VaultTextSecondary
                    )
                }
            }

            Spacer(Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .background(SectionBlue.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (card.cardType == CardType.DEBIT) "DEBIT" else "CREDIT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SectionBlue,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
