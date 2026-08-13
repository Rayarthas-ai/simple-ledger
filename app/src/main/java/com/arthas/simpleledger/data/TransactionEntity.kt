package com.arthas.simpleledger.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMinor: Long,
    val currency: CurrencyCode,
    val category: Category,
    val type: TransactionType = TransactionType.EXPENSE,
    val timestamp: Long,
    val note: String? = null
)
