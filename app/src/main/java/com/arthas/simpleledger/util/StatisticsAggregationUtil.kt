package com.arthas.simpleledger.util

import com.arthas.simpleledger.data.CategoryExpenseSummary
import com.arthas.simpleledger.data.TransactionEntity
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.model.TransactionType

object StatisticsAggregationUtil {
    fun totalMinor(rows: List<CategoryExpenseSummary>): Long =
        rows.sumOf { it.amountMinor }

    fun summarizeCategories(
        transactions: List<TransactionEntity>,
        currency: CurrencyCode
    ): List<CategoryExpenseSummary> =
        transactions
            .asSequence()
            .filter { it.type == TransactionType.EXPENSE && it.currency == currency && it.amountMinor > 0L }
            .groupBy { it.category }
            .map { (category: Category, rows: List<TransactionEntity>) ->
                CategoryExpenseSummary(category, rows.sumOf { it.amountMinor })
            }
            .sortedByDescending { it.amountMinor }
}
