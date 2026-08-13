package com.arthas.simpleledger.util

import com.arthas.simpleledger.data.TransactionEntity
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsAggregationUtilTest {
    @Test
    fun summarizeCategories_groupsByCategory() {
        val rows = StatisticsAggregationUtil.summarizeCategories(
            listOf(
                transaction(10000, CurrencyCode.PHP, Category.FOOD),
                transaction(20000, CurrencyCode.PHP, Category.FOOD),
                transaction(5000, CurrencyCode.PHP, Category.TRANSPORT)
            ),
            CurrencyCode.PHP
        )

        assertEquals(Category.FOOD, rows[0].category)
        assertEquals(30000L, rows[0].amountMinor)
        assertEquals(Category.TRANSPORT, rows[1].category)
        assertEquals(5000L, rows[1].amountMinor)
    }

    @Test
    fun summarizeCategories_keepsCurrenciesSeparate() {
        val rows = StatisticsAggregationUtil.summarizeCategories(
            listOf(
                transaction(50000, CurrencyCode.PHP, Category.FOOD),
                transaction(50000, CurrencyCode.CNY, Category.FOOD)
            ),
            CurrencyCode.PHP
        )

        assertEquals(1, rows.size)
        assertEquals(50000L, rows.first().amountMinor)
    }

    private fun transaction(
        amountMinor: Long,
        currency: CurrencyCode,
        category: Category
    ): TransactionEntity =
        TransactionEntity(
            amountMinor = amountMinor,
            currency = currency,
            category = category,
            timestamp = 0L
        )
}
