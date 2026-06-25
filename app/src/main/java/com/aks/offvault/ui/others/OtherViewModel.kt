package com.aks.offvault.ui.others

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.model.Other
import com.aks.offvault.data.repository.OtherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class OtherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OtherRepository(
        VaultDatabase.getInstance(application).otherDao()
    )

    val others: StateFlow<List<Other>> = repository.allOthers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getOtherFlow(id: Long): Flow<Other?> = repository.getOther(id)

    fun insertOther(other: Other) {
        viewModelScope.launch { repository.insertOther(other) }
    }

    fun updateOther(other: Other) {
        viewModelScope.launch { repository.updateOther(other) }
    }

    fun deleteOther(other: Other) {
        viewModelScope.launch { repository.deleteOther(other) }
    }
}