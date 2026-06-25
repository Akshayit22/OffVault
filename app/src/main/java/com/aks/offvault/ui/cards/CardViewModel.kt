package com.aks.offvault.ui.cards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.model.Card
import com.aks.offvault.data.repository.CardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CardRepository(
        VaultDatabase.getInstance(application).cardDao()
    )

    val cards: StateFlow<List<Card>> = repository.allCards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getCardFlow(id: Long): Flow<Card?> = repository.getCard(id)

    fun insertCard(card: Card) {
        viewModelScope.launch { repository.insertCard(card) }
    }

    fun updateCard(card: Card) {
        viewModelScope.launch { repository.updateCard(card) }
    }

    fun deleteCard(card: Card) {
        viewModelScope.launch { repository.deleteCard(card) }
    }
}