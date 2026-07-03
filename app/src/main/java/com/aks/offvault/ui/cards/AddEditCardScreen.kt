package com.aks.offvault.ui.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.model.CardType
import com.aks.offvault.ui.components.VaultPrimaryButton
import com.aks.offvault.ui.theme.SectionBlue
import com.aks.offvault.ui.theme.VaultBackground
import com.aks.offvault.ui.theme.VaultMonoFontFamily
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import com.aks.offvault.ui.theme.VaultTextSecondary

/** Displays raw digits as XXXX XXXX XXXX XXXX without altering the stored value. */
private object CardNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        val formatted = buildString {
            raw.forEachIndexed { i, c ->
                if (i > 0 && i % 4 == 0) append(' ')
                append(c)
            }
        }
        val offsetMapping = object : OffsetMapping {
            // Each group of 4 raw digits adds 1 space before the next group
            override fun originalToTransformed(offset: Int): Int =
                (offset + offset / 4).coerceAtMost(formatted.length)

            override fun transformedToOriginal(offset: Int): Int {
                val spaces = formatted.take(offset).count { it == ' ' }
                return (offset - spaces).coerceAtMost(raw.length)
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

private val fieldColors
    @Composable get() = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = SectionBlue,
        unfocusedBorderColor = VaultSurfaceBorder,
        focusedContainerColor = MaterialTheme.colorScheme.surface,
        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
        cursorColor = SectionBlue
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCardScreen(
    viewModel: CardViewModel,
    editCardId: Long? = null,
    onSaved: () -> Unit,
    onBackClick: () -> Unit
) {
    val isEditMode = editCardId != null
    val existingCard by if (isEditMode) {
        viewModel.getCardFlow(editCardId!!).collectAsState(initial = null)
    } else {
        remember { mutableStateOf(null) }
    }

    var label by rememberSaveable { mutableStateOf("") }
    var cardNumber by rememberSaveable { mutableStateOf("") }
    var expiryMonth by rememberSaveable { mutableStateOf("") }
    var expiryYear by rememberSaveable { mutableStateOf("") }
    var cardType by rememberSaveable { mutableStateOf(CardType.DEBIT) }
    var bankName by rememberSaveable { mutableStateOf("") }
    var cvv by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var cvvVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(existingCard) {
        existingCard?.let { card ->
            label = card.label
            cardNumber = card.cardNumber
            expiryMonth = card.expiryMonth
            expiryYear = card.expiryYear
            cardType = card.cardType
            bankName = card.bankName
            cvv = card.cvv
            notes = card.notes
        }
    }

    val isFormValid = cardNumber.length == 16 &&
            expiryMonth.length == 2 &&
            expiryYear.length == 2 &&
            cvv.length == 3

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditMode) "Edit Card" else "Add Card",
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
                    text = if (isEditMode) "Save Changes" else "Add Card",
                    enabled = isFormValid,
                    containerColor = SectionBlue,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        val card = Card(
                            id = if (isEditMode) existingCard!!.id else 0,
                            label = label.trim(),
                            cardNumber = cardNumber,
                            expiryMonth = expiryMonth,
                            expiryYear = expiryYear,
                            cardType = cardType,
                            bankName = bankName.trim(),
                            cvv = cvv,
                            notes = notes.trim(),
                            createdAt = if (isEditMode) existingCard!!.createdAt else System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        if (isEditMode) viewModel.updateCard(card) else viewModel.insertCard(card)
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
            // Card type — Debit / Credit segmented toggle
            SectionLabel("Card Type *")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = cardType == CardType.DEBIT,
                    onClick = { cardType = CardType.DEBIT },
                    label = { Text("Debit") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SectionBlue.copy(alpha = 0.2f),
                        selectedLabelColor = SectionBlue
                    )
                )
                FilterChip(
                    selected = cardType == CardType.CREDIT,
                    onClick = { cardType = CardType.CREDIT },
                    label = { Text("Credit") },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SectionBlue.copy(alpha = 0.2f),
                        selectedLabelColor = SectionBlue
                    )
                )
            }

            // Card Number — stores raw digits, displays with spaces, mono font
            SectionLabel("Card Number *")
            OutlinedTextField(
                value = cardNumber,
                onValueChange = { if (it.length <= 16 && it.all { c -> c.isDigit() }) cardNumber = it },
                placeholder = { Text("XXXX XXXX XXXX XXXX") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = VaultMonoFontFamily),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                visualTransformation = CardNumberVisualTransformation,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // Expiry
            SectionLabel("Expiry Date (MM/YY) *")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = expiryMonth,
                    onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryMonth = it },
                    placeholder = { Text("MM") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = expiryYear,
                    onValueChange = { if (it.length <= 2 && it.all { c -> c.isDigit() }) expiryYear = it },
                    placeholder = { Text("YY") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = fieldColors,
                    modifier = Modifier.weight(1f)
                )
            }

            // Bank Name
            SectionLabel("Bank Name")
            OutlinedTextField(
                value = bankName,
                onValueChange = { bankName = it },
                placeholder = { Text("e.g. HDFC, Axis, SBI") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // Label / Nickname
            SectionLabel("Card Label / Nickname")
            OutlinedTextField(
                value = label,
                onValueChange = { label = it },
                placeholder = { Text("e.g. My HDFC Salary Card") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // CVV
            SectionLabel("CVV *")
            OutlinedTextField(
                value = cvv,
                onValueChange = { if (it.length <= 3 && it.all { c -> c.isDigit() }) cvv = it },
                placeholder = { Text("3-digit CVV") },
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = VaultMonoFontFamily),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = if (cvvVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { cvvVisible = !cvvVisible }) {
                        Icon(
                            if (cvvVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = if (cvvVisible) "Hide CVV" else "Show CVV",
                            tint = VaultTextSecondary
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            // Notes / Info
            SectionLabel("Notes / Info")
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("Additional info (optional)") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(14.dp),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(88.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = VaultTextSecondary,
        fontWeight = FontWeight.Medium
    )
}
