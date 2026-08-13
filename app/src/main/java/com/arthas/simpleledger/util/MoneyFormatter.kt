package com.arthas.simpleledger.util

import com.arthas.simpleledger.model.CurrencyCode
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object MoneyFormatter {
    private val displayFormat = DecimalFormat("#,##0.##")

    fun parseToMinor(input: String): Long? {
        val normalized = input.trim()
        if (!normalized.matches(Regex("""\d+(\.\d{1,2})?"""))) return null
        val amount = normalized.toBigDecimalOrNull() ?: return null
        if (amount <= BigDecimal.ZERO) return null
        return amount.movePointRight(2).setScale(0, RoundingMode.UNNECESSARY).longValueExact()
    }

    fun formatMinor(amountMinor: Long, currency: CurrencyCode, withSymbol: Boolean = true): String {
        val value = BigDecimal(amountMinor).movePointLeft(2).stripTrailingZeros()
        val text = displayFormat.format(value)
        return if (withSymbol) "${currency.symbol}$text" else text
    }
}
