package com.aks.offvault.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aks.offvault.ui.theme.StrengthFair
import com.aks.offvault.ui.theme.StrengthGood
import com.aks.offvault.ui.theme.StrengthStrong
import com.aks.offvault.ui.theme.StrengthVeryWeak
import com.aks.offvault.ui.theme.StrengthWeak
import com.aks.offvault.ui.theme.VaultMonoFontFamily
import com.aks.offvault.ui.theme.VaultPrimary
import com.aks.offvault.ui.theme.VaultSurface
import com.aks.offvault.ui.theme.VaultSurfaceBorder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Standard elevated surface used for every card-like container across the app. */
@Composable
fun VaultCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cornerRadius: androidx.compose.ui.unit.Dp = 20.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(cornerRadius)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(VaultSurface)
            .border(BorderStroke(1.dp, VaultSurfaceBorder), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        content = content
    )
}

/** A colored, rounded icon tile — used for category icons, list-row icons, etc. */
@Composable
fun IconTile(
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 52.dp,
    iconSize: androidx.compose.ui.unit.Dp = 26.dp,
    cornerRadius: androidx.compose.ui.unit.Dp = 16.dp,
    fillAlpha: Float = 0.14f
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(cornerRadius))
            .background(tint.copy(alpha = fillAlpha)),
        contentAlignment = Alignment.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(iconSize))
    }
}

/** Primary filled call-to-action button with a soft accent shadow. */
@Composable
fun VaultPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = VaultPrimary,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            disabledContainerColor = containerColor.copy(alpha = 0.35f)
        ),
        modifier = modifier
            .height(54.dp)
            .then(
                if (enabled) Modifier.shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = containerColor.copy(alpha = 0.5f),
                    spotColor = containerColor.copy(alpha = 0.5f)
                ) else Modifier
            )
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

/** Outlined secondary button, e.g. Import Backup. */
@Composable
fun VaultOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = VaultPrimary,
    icon: ImageVector? = null
) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = accentColor),
        modifier = modifier.height(54.dp)
    ) {
        if (icon != null) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

/** Floating action button with a soft accent-colored shadow, consistent across sections. */
@Composable
fun VaultFab(
    onClick: () -> Unit,
    containerColor: Color,
    contentDescription: String,
    icon: ImageVector = Icons.Filled.Add
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = Color.White,
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier.shadow(
            elevation = 16.dp,
            shape = RoundedCornerShape(18.dp),
            ambientColor = containerColor.copy(alpha = 0.6f),
            spotColor = containerColor.copy(alpha = 0.6f)
        )
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

/** A rounded search field matching a section's accent color. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VaultSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onSearchAction: () -> Unit = {}
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = accentColor,
            unfocusedBorderColor = VaultSurfaceBorder,
            focusedContainerColor = VaultSurface,
            unfocusedContainerColor = VaultSurface,
            cursorColor = accentColor
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchAction() }),
        modifier = modifier.fillMaxWidth()
    )
}

// ── Password strength ────────────────────────────────────────────────────────

enum class PasswordStrength(val label: String, val color: Color, val fraction: Float) {
    VERY_WEAK("Very weak", StrengthVeryWeak, 0.2f),
    WEAK("Weak", StrengthWeak, 0.4f),
    FAIR("Fair", StrengthFair, 0.6f),
    GOOD("Good", StrengthGood, 0.8f),
    STRONG("Strong", StrengthStrong, 1.0f)
}

fun calculatePasswordStrength(password: String): PasswordStrength {
    if (password.isEmpty()) return PasswordStrength.VERY_WEAK
    var score = 0
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    return when {
        score <= 1 -> PasswordStrength.VERY_WEAK
        score == 2 -> PasswordStrength.WEAK
        score == 3 -> PasswordStrength.FAIR
        score == 4 -> PasswordStrength.GOOD
        else -> PasswordStrength.STRONG
    }
}

/** Thin rounded strength bar + label, used in login detail + add/edit screens. */
@Composable
fun StrengthMeter(strength: PasswordStrength, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(VaultSurfaceBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(strength.fraction)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(strength.color)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = strength.label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = strength.color
        )
    }
}

// ── Copy to clipboard with 30s auto-clear (security policy) ─────────────────

fun copyWithAutoClear(
    clipboardManager: ClipboardManager,
    scope: kotlinx.coroutines.CoroutineScope,
    value: String
) {
    clipboardManager.setText(AnnotatedString(value))
    scope.launch {
        delay(30_000L)
        clipboardManager.setText(AnnotatedString(""))
    }
}

/** A copy icon button that swaps to a checkmark for 2s after tapping. */
@Composable
fun CopyIconButton(onCopy: () -> Unit) {
    var justCopied by remember { mutableStateOf(false) }
    LaunchedEffect(justCopied) {
        if (justCopied) {
            delay(2_000L)
            justCopied = false
        }
    }
    IconButton(
        onClick = {
            onCopy()
            justCopied = true
        },
        modifier = Modifier.size(34.dp)
    ) {
        Icon(
            imageVector = if (justCopied) Icons.Outlined.CheckCircle else Icons.Outlined.ContentCopy,
            contentDescription = if (justCopied) "Copied" else "Copy",
            modifier = Modifier.size(18.dp),
            tint = if (justCopied) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Detail row for a label + a value that can be revealed/hidden and optionally copied. */
@Composable
fun SensitiveDetailRow(
    label: String,
    visibleValue: String,
    isVisible: Boolean,
    onToggle: () -> Unit,
    monospace: Boolean = true,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.34f)
        )
        Text(
            text = if (isVisible) visibleValue else "•".repeat(visibleValue.replace(" ", "").length.coerceAtMost(16)),
            fontFamily = if (monospace) VaultMonoFontFamily else null,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = if (isVisible) 1.sp else 2.sp,
            modifier = Modifier.weight(if (onCopy != null) 0.44f else 0.52f),
            textAlign = TextAlign.Start
        )
        IconButton(onClick = onToggle, modifier = Modifier.size(34.dp)) {
            Icon(
                imageVector = if (isVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                contentDescription = if (isVisible) "Hide" else "Show",
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onCopy != null) {
            CopyIconButton(onCopy = onCopy)
        }
    }
}

/** Detail row for a plain, always-visible label + value with an optional copy button. */
@Composable
fun CopyableDetailRow(
    label: String,
    value: String,
    monospace: Boolean = false,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.32f)
        )
        Text(
            text = value,
            fontFamily = if (monospace) VaultMonoFontFamily else null,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(if (onCopy != null) 0.48f else 0.68f),
            textAlign = TextAlign.Start
        )
        if (onCopy != null) {
            CopyIconButton(onCopy = onCopy)
        }
    }
}

/** Detail row for a plain, non-copyable, potentially multi-line value (e.g. Notes). */
@Composable
fun PlainDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.32f)
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(0.68f),
            textAlign = TextAlign.Start
        )
    }
}

/** Centered empty-state block used across every list screen. */
@Composable
fun EmptyState(
    icon: ImageVector,
    accentColor: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.padding(32.dp)
    ) {
        IconTile(
            icon = icon,
            tint = accentColor,
            size = 84.dp,
            iconSize = 40.dp,
            cornerRadius = 22.dp,
            fillAlpha = 0.12f
        )
        Spacer(Modifier.height(4.dp))
        Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(
            subtitle,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
