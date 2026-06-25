package com.aks.offvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CardType { DEBIT, CREDIT }

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String,
    val cardNumber: String,
    val expiryMonth: String,
    val expiryYear: String,
    val cardType: CardType,
    val bankName: String,
    val cvv: String,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
