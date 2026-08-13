package com.arthas.simpleledger.data

import androidx.room.TypeConverter
import com.arthas.simpleledger.model.Category
import com.arthas.simpleledger.model.CurrencyCode
import com.arthas.simpleledger.model.TransactionType

class Converters {
    @TypeConverter fun toCurrency(value: String): CurrencyCode = CurrencyCode.valueOf(value)
    @TypeConverter fun fromCurrency(value: CurrencyCode): String = value.name

    @TypeConverter fun toCategory(value: String): Category = Category.valueOf(value)
    @TypeConverter fun fromCategory(value: Category): String = value.name

    @TypeConverter fun toType(value: String): TransactionType = TransactionType.valueOf(value)
    @TypeConverter fun fromType(value: TransactionType): String = value.name
}
