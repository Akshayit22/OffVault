package com.aks.offvault.ui.home

import android.app.Application
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.VpnKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.repository.CardRepository
import com.aks.offvault.data.repository.DocumentRepository
import com.aks.offvault.data.repository.LoginDetailRepository
import com.aks.offvault.data.repository.OtherRepository
import com.aks.offvault.ui.theme.SectionBlue
import com.aks.offvault.ui.theme.SectionOrange
import com.aks.offvault.ui.theme.SectionPurple
import com.aks.offvault.ui.theme.SectionTeal
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    // Read-only counts — reuses the same repositories as every other screen purely to
    // render the "N items secured" summary and per-section count badges on Home.
    private val db = VaultDatabase.getInstance(application)
    private val cardRepo = CardRepository(db.cardDao())
    private val docRepo = DocumentRepository(db.documentDao())
    private val loginRepo = LoginDetailRepository(db.loginDetailDao())
    private val otherRepo = OtherRepository(db.otherDao())

    /** Personal, single-user vault — no account system, so this is a static display label. */
    val userName: String = "Akshay"

    val sectionCounts: StateFlow<Map<String, Int>> = combine(
        cardRepo.allCards,
        docRepo.allDocuments,
        loginRepo.allLoginDetails,
        otherRepo.allOthers
    ) { cards, docs, logins, others ->
        mapOf(
            "cards" to cards.size,
            "documents" to docs.size,
            "logins" to logins.size,
            "others" to others.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

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
