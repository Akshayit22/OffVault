package com.aks.offvault.ui.documents

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.model.Document
import com.aks.offvault.data.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DocumentRepository(
        VaultDatabase.getInstance(application).documentDao()
    )

    val documents: StateFlow<List<Document>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getDocumentFlow(id: Long): Flow<Document?> = repository.getDocument(id)

    fun insertDocument(document: Document) {
        viewModelScope.launch { repository.insertDocument(document) }
    }

    fun updateDocument(document: Document) {
        viewModelScope.launch { repository.updateDocument(document) }
    }

    fun deleteDocument(document: Document) {
        viewModelScope.launch { repository.deleteDocument(document) }
    }
}