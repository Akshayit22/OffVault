package com.aks.offvault.data.local

import androidx.room.TypeConverter
import com.aks.offvault.data.model.CardType

class Converters {
    @TypeConverter
    fun fromCardType(type: CardType): String = type.name

    @TypeConverter
    fun toCardType(value: String): CardType = CardType.valueOf(value)
}
