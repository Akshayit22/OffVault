package com.aks.offvault.data.repository

import com.aks.offvault.data.local.CardDao
import com.aks.offvault.data.model.Card
import kotlinx.coroutines.flow.Flow

class CardRepository(private val dao: CardDao) {

    val allCards: Flow<List<Card>> = dao.getAllCards()

    fun getCard(id: Long): Flow<Card?> = dao.getCardById(id)

    suspend fun insertCard(card: Card): Long = dao.insertCard(card)

    suspend fun updateCard(card: Card) = dao.updateCard(card)

    suspend fun deleteCard(card: Card) = dao.deleteCard(card)
}