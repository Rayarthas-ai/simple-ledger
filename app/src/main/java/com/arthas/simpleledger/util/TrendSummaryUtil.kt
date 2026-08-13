package com.arthas.simpleledger.util

import com.arthas.simpleledger.data.DailyExpenseSummary
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

data class TrendPoint(
    val key: String,
    val label: String,
    val amountMinor: Long
)

object TrendSummaryUtil {
    fun fillTrend(
        period: StatsPeriod,
        range: DateRange,
        rows: List<DailyExpenseSummary>,
        zone: ZoneId = ZoneId.systemDefault()
    ): List<TrendPoint> {
        return if (period == StatsPeriod.YEAR) {
            fillMonths(range, rows, zone)
        } else {
            fillDays(period, range, rows, zone)
        }
    }

    private fun fillDays(
        period: StatsPeriod,
        range: DateRange,
        rows: List<DailyExpenseSummary>,
        zone: ZoneId
    ): List<TrendPoint> {
        val byDate = rows.associateBy { it.date }
        val start = Instant.ofEpochMilli(range.startMillis).atZone(zone).toLocalDate()
        val end = Instant.ofEpochMilli(range.endMillis).atZone(zone).toLocalDate()
        val result = mutableListOf<TrendPoint>()
        var current = start
        while (current.isBefore(end)) {
            val key = current.toString()
            result += TrendPoint(
                key = key,
                label = when (period) {
                    StatsPeriod.WEEK -> current.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                    StatsPeriod.MONTH -> current.dayOfMonth.toString()
                    StatsPeriod.QUARTER -> "${current.monthValue}/${current.dayOfMonth}"
                    StatsPeriod.YEAR -> current.monthValue.toString()
                },
                amountMinor = byDate[key]?.amountMinor ?: 0L
            )
            current = current.plusDays(1)
        }
        return result
    }

    private fun fillMonths(
        range: DateRange,
        rows: List<DailyExpenseSummary>,
        zone: ZoneId
    ): List<TrendPoint> {
        val byMonth = rows.associateBy { it.date }
        val year = Instant.ofEpochMilli(range.startMillis).atZone(zone).year
        return (1..12).map { month ->
            val key = YearMonth.of(year, month).toString()
            TrendPoint(
                key = key,
                label = YearMonth.of(year, month).month.name.take(3).lowercase().replaceFirstChar { it.uppercase() },
                amountMinor = byMonth[key]?.amountMinor ?: 0L
            )
        }
    }
}
