package com.arthas.simpleledger.data

import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode

data class CurrencySummaryRow(
    val currency: CurrencyCode,
    val totalMinor: Long
)

data class CategorySummaryRow(
    val currency: CurrencyCode,
    val category: Category,
    val totalMinor: Long
)

data class CategoryExpenseSummary(
    val category: Category,
    val amountMinor: Long
)

data class DailyExpenseSummary(
    val date: String,
    val amountMinor: Long
)
