package com.arthas.simpleledger.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arthas.simpleledger.data.CategoryExpenseSummary
import com.arthas.simpleledger.data.TransactionRepository
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.util.DateRange
import com.arthas.simpleledger.util.DateRangeUtil
import com.arthas.simpleledger.util.StatisticsAggregationUtil
import com.arthas.simpleledger.util.StatsPeriod
import com.arthas.simpleledger.util.TrendPoint
import com.arthas.simpleledger.util.TrendSummaryUtil
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class StatisticsUiState(
    val period: StatsPeriod = StatsPeriod.MONTH,
    val currency: CurrencyCode = CurrencyCode.PHP,
    val range: DateRange = DateRangeUtil.currentRange(StatsPeriod.MONTH),
    val totalMinor: Long = 0L,
    val categoryTotals: List<CategoryExpenseSummary> = emptyList(),
    val trendPoints: List<TrendPoint> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class StatisticsViewModel(private val repository: TransactionRepository) : ViewModel() {
    private val period = MutableStateFlow(StatsPeriod.MONTH)
    private val currency = MutableStateFlow(CurrencyCode.PHP)

    val uiState: StateFlow<StatisticsUiState> = combine(period, currency) { period, currency ->
        period to currency
    }.flatMapLatest { (selectedPeriod, selectedCurrency) ->
        val selectedRange = DateRangeUtil.currentRange(selectedPeriod)
        val trendFlow = if (selectedPeriod == StatsPeriod.YEAR) {
            repository.observeMonthlyExpenseSummary(
                selectedRange.startMillis,
                selectedRange.endMillis,
                selectedCurrency
            )
        } else {
            repository.observeDailyExpenseSummary(
                selectedRange.startMillis,
                selectedRange.endMillis,
                selectedCurrency
            )
        }
        combine(
            repository.observeCategoryExpenseSummary(
                selectedRange.startMillis,
                selectedRange.endMillis,
                selectedCurrency
            ),
            trendFlow
        ) { categories, trendRows ->
            StatisticsUiState(
                period = selectedPeriod,
                currency = selectedCurrency,
                range = selectedRange,
                totalMinor = StatisticsAggregationUtil.totalMinor(categories),
                categoryTotals = categories,
                trendPoints = TrendSummaryUtil.fillTrend(selectedPeriod, selectedRange, trendRows)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StatisticsUiState())

    fun setPeriod(value: StatsPeriod) {
        period.value = value
    }

    fun setCurrency(value: CurrencyCode) {
        currency.value = value
    }
}
