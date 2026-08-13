package com.arthas.simpleledger.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthas.simpleledger.data.SettingsRepository
import com.arthas.simpleledger.data.TransactionEntity
import com.arthas.simpleledger.data.TransactionRepository
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.util.MoneyFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddUiState(
    val amount: String = "",
    val currency: CurrencyCode = CurrencyCode.PHP,
    val category: Category? = null,
    val note: String = "",
    val savedMessage: String? = null,
    val canSave: Boolean = false
)

class AddViewModel(
    private val repository: TransactionRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {
    private val amount = MutableStateFlow("")
    private val currency = MutableStateFlow(CurrencyCode.PHP)
    private val category = MutableStateFlow<Category?>(null)
    private val note = MutableStateFlow("")
    private val savedMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<AddUiState> =
        combine(amount, currency, category, note, savedMessage) { amount, currency, category, note, message ->
            AddUiState(
                amount = amount,
                currency = currency,
                category = category,
                note = note,
                savedMessage = message,
                canSave = MoneyFormatter.parseToMinor(amount) != null && category != null
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddUiState())

    init {
        viewModelScope.launch {
            settingsRepository.defaultCurrency.collect { currency.value = it }
        }
    }

    fun setAmount(value: String) {
        if (value.isEmpty() || value.matches(Regex("""\d{0,9}(\.\d{0,2})?"""))) {
            amount.value = value
            savedMessage.value = null
        }
    }

    fun setCurrency(value: CurrencyCode) {
        currency.value = value
    }

    fun setCategory(value: Category) {
        category.value = value
    }

    fun setNote(value: String) {
        note.value = value.take(80)
    }

    fun save() {
        val minor = MoneyFormatter.parseToMinor(amount.value) ?: return
        val selectedCategory = category.value ?: return
        viewModelScope.launch {
            repository.add(
                TransactionEntity(
                    amountMinor = minor,
                    currency = currency.value,
                    category = selectedCategory,
                    timestamp = System.currentTimeMillis(),
                    note = note.value.ifBlank { null }
                )
            )
            amount.value = ""
            note.value = ""
            savedMessage.value = "已保存"
        }
    }
}
