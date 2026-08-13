package com.arthas.simpleledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.arthas.simpleledger.data.SettingsRepository
import com.arthas.simpleledger.data.TransactionRepository
import com.arthas.simpleledger.ui.add.AddViewModel
import com.arthas.simpleledger.ui.history.HistoryViewModel
import com.arthas.simpleledger.ui.settings.SettingsViewModel
import com.arthas.simpleledger.ui.statistics.StatisticsViewModel

class AppViewModelFactory(
    private val transactionRepository: TransactionRepository,
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AddViewModel::class.java) ->
                AddViewModel(transactionRepository, settingsRepository) as T
            modelClass.isAssignableFrom(HistoryViewModel::class.java) ->
                HistoryViewModel(transactionRepository) as T
            modelClass.isAssignableFrom(StatisticsViewModel::class.java) ->
                StatisticsViewModel(transactionRepository) as T
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(settingsRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
