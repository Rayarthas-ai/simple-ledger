package com.arthas.simpleledger.ui.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.util.MoneyFormatter
import com.arthas.simpleledger.util.StatsPeriod

@Composable
fun StatisticsScreen(viewModel: StatisticsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatsPeriod.entries.forEach { period ->
                FilterChip(
                    selected = state.period == period,
                    onClick = { viewModel.setPeriod(period) },
                    label = { Text(period.label) }
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CurrencyCode.entries.forEach { currency ->
                FilterChip(
                    selected = state.currency == currency,
                    onClick = { viewModel.setCurrency(currency) },
                    label = { Text(currency.name) }
                )
            }
        }

        Text("${state.range.label}  ${state.currency.name}")
        Text("总支出：${MoneyFormatter.formatMinor(state.totalMinor, state.currency, withSymbol = false)}")

        CategoryPieChart(
            rows = state.categoryTotals,
            totalMinor = state.totalMinor,
            currency = state.currency
        )

        HorizontalDivider()

        ExpenseLineChart(
            points = state.trendPoints,
            currency = state.currency,
            title = if (state.period == StatsPeriod.YEAR) "每月支出趋势" else "每日支出趋势"
        )

        HorizontalDivider()

        if (state.totalMinor <= 0L) {
            Text("${state.range.label} 暂无 ${state.currency.name} 支出")
        } else {
            Text("分类明细")
            state.categoryTotals.forEach { row ->
                val percent = row.amountMinor.toDouble() * 100.0 / state.totalMinor.toDouble()
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(row.category.label)
                    Text("${MoneyFormatter.formatMinor(row.amountMinor, state.currency, false)}  ${"%.1f".format(percent)}%")
                }
            }
        }
    }
}
