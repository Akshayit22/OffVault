package com.aks.offvault.data.repository

import com.aks.offvault.data.local.LoginDetailDao
import com.aks.offvault.data.model.LoginDetail
import kotlinx.coroutines.flow.Flow

class LoginDetailRepository(private val dao: LoginDetailDao) {

    val allLoginDetails: Flow<List<LoginDetail>> = dao.getAllLoginDetails()

    fun getLoginDetail(id: Long): Flow<LoginDetail?> = dao.getLoginDetailById(id)

    suspend fun insertLoginDetail(loginDetail: LoginDetail): Long = dao.insertLoginDetail(loginDetail)

    suspend fun updateLoginDetail(loginDetail: LoginDetail) = dao.updateLoginDetail(loginDetail)

    suspend fun deleteLoginDetail(loginDetail: LoginDetail) = dao.deleteLoginDetail(loginDetail)
}