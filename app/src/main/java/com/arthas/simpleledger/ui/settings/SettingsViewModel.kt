package com.arthas.simpleledger.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthas.simpleledger.data.SettingsRepository
import com.arthas.simpleledger.model.CurrencyCode
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val settings: SettingsRepository) : ViewModel() {
    val defaultCurrency: StateFlow<CurrencyCode> =
        settings.defaultCurrency.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CurrencyCode.PHP)

    fun setDefaultCurrency(currency: CurrencyCode) {
        viewModelScope.launch { settings.setDefaultCurrency(currency) }
    }
}
