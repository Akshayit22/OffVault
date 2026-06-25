package com.aks.offvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "others")
data class Other(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val info: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)