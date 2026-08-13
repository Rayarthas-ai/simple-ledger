package com.arthas.simpleledger.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity)

    @Delete
    suspend fun delete(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC, id DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): TransactionEntity?

    @Query("""
        SELECT * FROM transactions
        WHERE timestamp >= :startTimestamp AND timestamp < :endTimestamp
        ORDER BY timestamp DESC, id DESC
    """)
    fun observeBetween(startTimestamp: Long, endTimestamp: Long): Flow<List<TransactionEntity>>

    @Query("""
        SELECT currency, SUM(amountMinor) AS totalMinor
        FROM transactions
        WHERE type = 'EXPENSE'
          AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
        GROUP BY currency
        ORDER BY currency
    """)
    fun observeCurrencySummary(startTimestamp: Long, endTimestamp: Long): Flow<List<CurrencySummaryRow>>

    @Query("""
        SELECT currency, category, SUM(amountMinor) AS totalMinor
        FROM transactions
        WHERE type = 'EXPENSE'
          AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
        GROUP BY currency, category
        ORDER BY currency, totalMinor DESC
    """)
    fun observeCategorySummary(startTimestamp: Long, endTimestamp: Long): Flow<List<CategorySummaryRow>>

    @Query("""
        SELECT category, SUM(amountMinor) AS amountMinor
        FROM transactions
        WHERE type = 'EXPENSE'
          AND currency = :currency
          AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
        GROUP BY category
        HAVING amountMinor > 0
        ORDER BY amountMinor DESC
    """)
    fun observeCategoryExpenseSummary(
        startTimestamp: Long,
        endTimestamp: Long,
        currency: CurrencyCode
    ): Flow<List<CategoryExpenseSummary>>

    @Query("""
        SELECT strftime('%Y-%m-%d', timestamp / 1000, 'unixepoch', 'localtime') AS date,
               SUM(amountMinor) AS amountMinor
        FROM transactions
        WHERE type = 'EXPENSE'
          AND currency = :currency
          AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
        GROUP BY date
        ORDER BY date
    """)
    fun observeDailyExpenseSummary(
        startTimestamp: Long,
        endTimestamp: Long,
        currency: CurrencyCode
    ): Flow<List<DailyExpenseSummary>>

    @Query("""
        SELECT strftime('%Y-%m', timestamp / 1000, 'unixepoch', 'localtime') AS date,
               SUM(amountMinor) AS amountMinor
        FROM transactions
        WHERE type = 'EXPENSE'
          AND currency = :currency
          AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
        GROUP BY date
        ORDER BY date
    """)
    fun observeMonthlyExpenseSummary(
        startTimestamp: Long,
        endTimestamp: Long,
        currency: CurrencyCode
    ): Flow<List<DailyExpenseSummary>>

    @Query("""
        SELECT * FROM transactions
        WHERE (:currency IS NULL OR currency = :currency)
          AND (:category IS NULL OR category = :category)
        ORDER BY timestamp DESC, id DESC
    """)
    fun observeFiltered(currency: CurrencyCode?, category: Category?): Flow<List<TransactionEntity>>
}
