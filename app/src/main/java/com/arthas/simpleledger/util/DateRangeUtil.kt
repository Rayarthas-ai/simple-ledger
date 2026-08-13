package com.arthas.simpleledger.util

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

enum class StatsPeriod(val label: String) {
    WEEK("周"),
    MONTH("月"),
    QUARTER("季度"),
    YEAR("年")
}

data class DateRange(val startMillis: Long, val endMillis: Long, val label: String)

object DateRangeUtil {
    fun currentRange(
        period: StatsPeriod,
        nowMillis: Long = System.currentTimeMillis(),
        zone: ZoneId = ZoneId.systemDefault()
    ): DateRange {
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        return rangeFor(today, period, zone)
    }

    fun rangeFor(date: LocalDate, period: StatsPeriod, zone: ZoneId = ZoneId.systemDefault()): DateRange {
        val start = when (period) {
            StatsPeriod.WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            StatsPeriod.MONTH -> date.withDayOfMonth(1)
            StatsPeriod.QUARTER -> {
                val firstMonth = ((date.monthValue - 1) / 3) * 3 + 1
                LocalDate.of(date.year, firstMonth, 1)
            }
            StatsPeriod.YEAR -> LocalDate.of(date.year, 1, 1)
        }
        val end = when (period) {
            StatsPeriod.WEEK -> start.plusWeeks(1)
            StatsPeriod.MONTH -> start.plusMonths(1)
            StatsPeriod.QUARTER -> start.plusMonths(3)
            StatsPeriod.YEAR -> start.plusYears(1)
        }
        val label = when (period) {
            StatsPeriod.WEEK -> "${start} ~ ${end.minusDays(1)}"
            StatsPeriod.MONTH -> "%04d-%02d".format(start.year, start.monthValue)
            StatsPeriod.QUARTER -> "${start.year} Q${((start.monthValue - 1) / 3) + 1}"
            StatsPeriod.YEAR -> start.year.toString()
        }
        return DateRange(
            start.atStartOfDay(zone).toInstant().toEpochMilli(),
            end.atStartOfDay(zone).toInstant().toEpochMilli(),
            label
        )
    }
}
