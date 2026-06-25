package com.aks.offvault.data.repository

import com.aks.offvault.data.local.OtherDao
import com.aks.offvault.data.model.Other
import kotlinx.coroutines.flow.Flow

class OtherRepository(private val dao: OtherDao) {

    val allOthers: Flow<List<Other>> = dao.getAllOthers()

    fun getOther(id: Long): Flow<Other?> = dao.getOtherById(id)

    suspend fun insertOther(other: Other): Long = dao.insertOther(other)

    suspend fun updateOther(other: Other) = dao.updateOther(other)

    suspend fun deleteOther(other: Other) = dao.deleteOther(other)
}