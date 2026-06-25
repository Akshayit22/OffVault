package com.aks.offvault.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "login_details")
data class LoginDetail(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val username: String,
    val password: String,
    val info: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)