package com.aks.offvault.ui.logins

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aks.offvault.data.local.VaultDatabase
import com.aks.offvault.data.model.LoginDetail
import com.aks.offvault.data.repository.LoginDetailRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = LoginDetailRepository(
        VaultDatabase.getInstance(application).loginDetailDao()
    )

    val loginDetails: StateFlow<List<LoginDetail>> = repository.allLoginDetails
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun getLoginDetailFlow(id: Long): Flow<LoginDetail?> = repository.getLoginDetail(id)

    fun insertLoginDetail(loginDetail: LoginDetail) {
        viewModelScope.launch { repository.insertLoginDetail(loginDetail) }
    }

    fun updateLoginDetail(loginDetail: LoginDetail) {
        viewModelScope.launch { repository.updateLoginDetail(loginDetail) }
    }

    fun deleteLoginDetail(loginDetail: LoginDetail) {
        viewModelScope.launch { repository.deleteLoginDetail(loginDetail) }
    }
}