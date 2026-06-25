package com.aks.offvault.ui.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.VpnKey
import androidx.lifecycle.ViewModel
import com.aks.offvault.ui.theme.SectionBlue
import com.aks.offvault.ui.theme.SectionOrange
import com.aks.offvault.ui.theme.SectionPurple
import com.aks.offvault.ui.theme.SectionTeal

class HomeViewModel : ViewModel() {

    val sections: List<SectionItem> = listOf(
        SectionItem(
            id = "cards",
            label = "Cards",
            icon = Icons.Outlined.CreditCard,
            description = "Debit & credit cards",
            accentColor = SectionBlue
        ),
        SectionItem(
            id = "documents",
            label = "Documents",
            icon = Icons.Outlined.Description,
            description = "IDs, passports & more",
            accentColor = SectionTeal
        ),
        SectionItem(
            id = "logins",
            label = "Logins",
            icon = Icons.Outlined.VpnKey,
            description = "Usernames & passwords",
            accentColor = SectionPurple
        ),
        SectionItem(
            id = "others",
            label = "Others",
            icon = Icons.Outlined.Category,
            description = "Bank accounts & more",
            accentColor = SectionOrange
        )
    )
}