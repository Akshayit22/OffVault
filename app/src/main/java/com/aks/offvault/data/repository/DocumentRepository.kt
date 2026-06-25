package com.aks.offvault.data.repository

import com.aks.offvault.data.local.DocumentDao
import com.aks.offvault.data.model.Document
import kotlinx.coroutines.flow.Flow

class DocumentRepository(private val dao: DocumentDao) {

    val allDocuments: Flow<List<Document>> = dao.getAllDocuments()

    fun getDocument(id: Long): Flow<Document?> = dao.getDocumentById(id)

    suspend fun insertDocument(document: Document): Long = dao.insertDocument(document)

    suspend fun updateDocument(document: Document) = dao.updateDocument(document)

    suspend fun deleteDocument(document: Document) = dao.deleteDocument(document)
}