package com.aks.offvault.ui.data

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.backup.BackupCrypto
import com.aks.offvault.data.backup.BackupSerializer
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.repository.CardRepository
import com.aks.offvault.data.repository.DocumentRepository
import com.aks.offvault.data.repository.LoginDetailRepository
import com.aks.offvault.data.repository.OtherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VaultStats(
    val cardCount: Int = 0,
    val documentCount: Int = 0,
    val loginCount: Int = 0,
    val otherCount: Int = 0
)

sealed class DataUiState {
    object Idle : DataUiState()
    object Loading : DataUiState()
    data class Success(val message: String) : DataUiState()
    data class Error(val message: String) : DataUiState()
}

class DataViewModel(application: Application) : AndroidViewModel(application) {

    private val db = VaultDatabase.getInstance(application)
    private val cardRepo = CardRepository(db.cardDao())
    private val docRepo = DocumentRepository(db.documentDao())
    private val loginRepo = LoginDetailRepository(db.loginDetailDao())
    private val otherRepo = OtherRepository(db.otherDao())

    val stats: StateFlow<VaultStats> = combine(
        cardRepo.allCards,
        docRepo.allDocuments,
        loginRepo.allLoginDetails,
        otherRepo.allOthers
    ) { cards, docs, logins, others ->
        VaultStats(cards.size, docs.size, logins.size, others.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), VaultStats())

    private val _uiState = MutableStateFlow<DataUiState>(DataUiState.Idle)
    val uiState: StateFlow<DataUiState> = _uiState

    fun exportBackup(passphrase: String, uri: Uri) {
        viewModelScope.launch {
            _uiState.update { DataUiState.Loading }
            try {
                val cards = cardRepo.allCards.first()
                val docs = docRepo.allDocuments.first()
                val logins = loginRepo.allLoginDetails.first()
                val others = otherRepo.allOthers.first()

                val json = BackupSerializer.serialize(cards, docs, logins, others)
                val encrypted = BackupCrypto.encrypt(json.toByteArray(Charsets.UTF_8), passphrase)

                getApplication<Application>().contentResolver.openOutputStream(uri)?.use {
                    it.write(encrypted)
                } ?: throw IllegalStateException("Could not open output stream")

                _uiState.update { DataUiState.Success("Backup exported successfully") }
            } catch (e: Exception) {
                _uiState.update { DataUiState.Error("Export failed: ${e.message}") }
            }
        }
    }

    fun importBackup(passphrase: String, uri: Uri, replaceExisting: Boolean) {
        viewModelScope.launch {
            _uiState.update { DataUiState.Loading }
            try {
                val encrypted = getApplication<Application>().contentResolver
                    .openInputStream(uri)?.use { it.readBytes() }
                    ?: throw IllegalStateException("Could not read backup file")

                val json = try {
                    BackupCrypto.decrypt(encrypted, passphrase).toString(Charsets.UTF_8)
                } catch (e: Exception) {
                    _uiState.update { DataUiState.Error("Wrong passphrase or corrupted file") }
                    return@launch
                }

                val data = BackupSerializer.deserialize(json)

                if (replaceExisting) {
                    db.cardDao().deleteAll()
                    db.documentDao().deleteAll()
                    db.loginDetailDao().deleteAll()
                    db.otherDao().deleteAll()
                }

                data.cards.forEach { db.cardDao().insertCard(it) }
                data.documents.forEach { db.documentDao().insertDocument(it) }
                data.loginDetails.forEach { db.loginDetailDao().insertLoginDetail(it) }
                data.others.forEach { db.otherDao().insertOther(it) }

                val verb = if (replaceExisting) "restored" else "merged"
                _uiState.update { DataUiState.Success("Data $verb successfully") }
            } catch (e: Exception) {
                _uiState.update { DataUiState.Error("Import failed: ${e.message}") }
            }
        }
    }

    fun clearState() {
        _uiState.update { DataUiState.Idle }
    }
}
