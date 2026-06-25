package com.aks.offvault.ui.cards

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.CardType
import com.aks.offvault.ui.theme.SectionBlue

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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
            Text("Card not found", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun CardVisual(card: Card, cardNumberVisible: Boolean) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1A3A6B), SectionBlue.copy(alpha = 0.8f))
    )
    val displayNumber = if (cardNumberVisible) {
        card.cardNumber.chunked(4).joinToString("  ")
    } else {
        "••••  ••••  ••••  ${card.cardNumber.takeLast(4)}"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(gradient)
            .padding(20.dp)
    ) {
        // Card type badge
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White.copy(alpha = 0.15f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = if (card.cardType == CardType.DEBIT) "DEBIT" else "CREDIT",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Bank name
        Text(
            text = card.bankName.uppercase().ifBlank { "—" },
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Masked card number
        Text(
            text = displayNumber,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp,
            modifier = Modifier.align(Alignment.Center)
        )

        // Expiry bottom-right
        Text(
            text = "${card.expiryMonth}/${card.expiryYear}",
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
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

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 4.dp)
    ) {
        // Full card number — shares visibility state with the card visual above
        SensitiveDetailRow(
            label = "Card Number",
            visibleValue = card.cardNumber.formatCardNumber(),
            isVisible = cardNumberVisible,
            onToggle = onCardNumberToggle,
            onCopy = {
                clipboardManager.setText(AnnotatedString(card.cardNumber.formatCardNumber()))
                // CLAUDE.md §6: clear clipboard after 30 seconds
                scope.launch {
                    delay(30_000L)
                    clipboardManager.setText(AnnotatedString(""))
                }
            }
        )

        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

        // CVV
        SensitiveDetailRow(
            label = "CVV",
            visibleValue = card.cvv,
            isVisible = cvvVisible,
            onToggle = { cvvVisible = !cvvVisible }
        )

        // Label (only if set)
        if (card.label.isNotBlank()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            DetailRow(label = "Label", value = card.label)
        }

        // Notes (only if set)
        if (card.notes.isNotBlank()) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            DetailRow(label = "Notes", value = card.notes)
        }
    }
}

@Composable
private fun SensitiveDetailRow(
    label: String,
    visibleValue: String,
    isVisible: Boolean,
    onToggle: () -> Unit,
    onCopy: (() -> Unit)? = null
) {
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
            modifier = Modifier.weight(0.35f)
        )
        Text(
            text = if (isVisible) visibleValue else "•".repeat(visibleValue.replace(" ", "").length),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            letterSpacing = if (isVisible) 1.sp else 2.sp,
            modifier = Modifier.weight(if (onCopy != null) 0.45f else 0.52f),
            textAlign = TextAlign.Start
        )
        IconButton(
            onClick = onToggle,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (isVisible) "Hide" else "Show",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onCopy != null) {
            IconButton(
                onClick = {
                    onCopy()
                    justCopied = true
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (justCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
                    contentDescription = if (justCopied) "Copied" else "Copy card number",
                    modifier = Modifier.size(18.dp),
                    tint = if (justCopied) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
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
            modifier = Modifier.weight(0.38f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(0.62f),
            textAlign = TextAlign.Start
        )
    }
}
