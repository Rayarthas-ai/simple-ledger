package com.arthas.simpleledger.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthas.simpleledger.data.TransactionEntity
import com.arthas.simpleledger.data.TransactionRepository
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HistoryFilter(val currency: CurrencyCode? = null, val category: Category? = null)
data class HistoryUiState(val transactions: List<TransactionEntity> = emptyList(), val filter: HistoryFilter = HistoryFilter())

@OptIn(ExperimentalCoroutinesApi::class)
class HistoryViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val filter = MutableStateFlow(HistoryFilter())

    val uiState: StateFlow<HistoryUiState> = filter
        .flatMapLatest { filter ->
            repository.observeFiltered(filter.currency, filter.category).map { HistoryUiState(it, filter) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())

    fun setCurrency(currency: CurrencyCode?) {
        filter.value = filter.value.copy(currency = currency)
    }

    fun setCategory(category: Category?) {
        filter.value = filter.value.copy(category = category)
    }

    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch { repository.delete(transaction) }
    }

    fun update(transaction: TransactionEntity) {
        viewModelScope.launch { repository.update(transaction) }
    }
}
