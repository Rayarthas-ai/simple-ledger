package com.arthas.simpleledger.data

import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val dao: TransactionDao) {
    fun observeAll(): Flow<List<TransactionEntity>> = dao.observeAll()
    fun observeFiltered(currency: CurrencyCode?, category: Category?): Flow<List<TransactionEntity>> =
        dao.observeFiltered(currency, category)

    fun observeCurrencySummary(start: Long, end: Long): Flow<List<CurrencySummaryRow>> =
        dao.observeCurrencySummary(start, end)

    fun observeCategorySummary(start: Long, end: Long): Flow<List<CategorySummaryRow>> =
        dao.observeCategorySummary(start, end)

    fun observeCategoryExpenseSummary(
        start: Long,
        end: Long,
        currency: CurrencyCode
    ): Flow<List<CategoryExpenseSummary>> = dao.observeCategoryExpenseSummary(start, end, currency)

    fun observeDailyExpenseSummary(
        start: Long,
        end: Long,
        currency: CurrencyCode
    ): Flow<List<DailyExpenseSummary>> = dao.observeDailyExpenseSummary(start, end, currency)

    fun observeMonthlyExpenseSummary(
        start: Long,
        end: Long,
        currency: CurrencyCode
    ): Flow<List<DailyExpenseSummary>> = dao.observeMonthlyExpenseSummary(start, end, currency)

    suspend fun add(transaction: TransactionEntity): Long = dao.insert(transaction)
    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
    suspend fun getById(id: Long): TransactionEntity? = dao.getById(id)
}
