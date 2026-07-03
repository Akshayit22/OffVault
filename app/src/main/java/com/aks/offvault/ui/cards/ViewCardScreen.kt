package com.aks.offvault.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.outlined.Delete
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.CardType
import com.aks.offvault.ui.components.CopyableDetailRow
import com.aks.offvault.ui.components.PlainDetailRow
import com.aks.offvault.ui.components.SensitiveDetailRow
import com.aks.offvault.ui.components.VaultCard
import com.aks.offvault.ui.components.copyWithAutoClear
import com.aks.offvault.ui.theme.SectionBlue
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultMonoFontFamily
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

/** Returns the card number formatted as XXXX XXXX XXXX XXXX. */
private fun String.formatCardNumber() = chunked(4).joinToString(" ")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewCardScreen(
    viewModel: CardViewModel,
    cardId: Long,
    onEditClick: () -> Unit,
    onDeleted: () -> Unit,
    onBackClick: () -> Unit
) {
    val card by viewModel.getCardFlow(cardId).collectAsState(initial = null)
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var cardNumberVisible by rememberSaveable { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Card") },
            text = { Text("This card will be permanently deleted. This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    card?.let { viewModel.deleteCard(it) }
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
                        text = card?.label?.ifBlank { card?.bankName ?: "Card" } ?: "Card",
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
                        Icon(Icons.Outlined.Edit, contentDescription = "Edit card")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Outlined.Delete,
                            contentDescription = "Delete card",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = VaultBackground)
            )
        },
        containerColor = VaultBackground
    ) { innerPadding ->
        card?.let { c ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CardVisual(card = c, cardNumberVisible = cardNumberVisible)
                CardDetailsSection(
                    card = c,
                    cardNumberVisible = cardNumberVisible,
                    onCardNumberToggle = { cardNumberVisible = !cardNumberVisible }
                )
            }
        } ?: Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text("Card not found", color = VaultTextSecondary)
        }
    }
}

@Composable
private fun CardVisual(card: Card, cardNumberVisible: Boolean) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1E3A8A), SectionBlue)
    )
    val displayNumber = if (cardNumberVisible) {
        card.cardNumber.chunked(4).joinToString("  ")
    } else {
        "••••  ••••  ••••  ${card.cardNumber.takeLast(4)}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(22.dp)
    ) {
        // Top row: bank name + type chip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
        ) {
            Text(
                text = card.bankName.uppercase().ifBlank { "OFFVAULT" },
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (card.cardType == CardType.DEBIT) "DEBIT" else "CREDIT",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Gold chip graphic
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(y = (-18).dp)
                .size(width = 42.dp, height = 32.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFDE68A), Color(0xFFD4AF37))
                    )
                )
        )

        // Masked / revealed card number
        Text(
            text = displayNumber,
            color = Color.White,
            fontFamily = VaultMonoFontFamily,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        // Contactless icon
        Icon(
            imageVector = Icons.Filled.Wifi,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = (-18).dp)
                .size(26.dp)
                .rotate(90f)
        )

        // Bottom row: VALID THRU
        Column(modifier = Modifier.align(Alignment.BottomStart)) {
            Text(
                text = "VALID THRU",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "${card.expiryMonth}/${card.expiryYear}",
                color = Color.White,
                fontFamily = VaultMonoFontFamily,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun CardDetailsSection(
    card: Card,
    cardNumberVisible: Boolean,
    onCardNumberToggle: () -> Unit
) {
    var cvvVisible by rememberSaveable { mutableStateOf(false) }
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()

    VaultCard {
        // Full card number — shares visibility state with the card visual above
        SensitiveDetailRow(
            label = "Card Number",
            visibleValue = card.cardNumber.formatCardNumber(),
            isVisible = cardNumberVisible,
            onToggle = onCardNumberToggle,
            onCopy = {
                copyWithAutoClear(clipboardManager, scope, card.cardNumber.formatCardNumber())
            }
        )

        HorizontalDivider(color = VaultSurfaceBorder)

        // CVV
        SensitiveDetailRow(
            label = "CVV",
            visibleValue = card.cvv,
            isVisible = cvvVisible,
            onToggle = { cvvVisible = !cvvVisible }
        )

        // Label (only if set)
        if (card.label.isNotBlank()) {
            HorizontalDivider(color = VaultSurfaceBorder)
            PlainDetailRow(label = "Label", value = card.label)
        }

        // Notes (only if set)
        if (card.notes.isNotBlank()) {
            HorizontalDivider(color = VaultSurfaceBorder)
            PlainDetailRow(label = "Notes", value = card.notes)
        }
    }
}
