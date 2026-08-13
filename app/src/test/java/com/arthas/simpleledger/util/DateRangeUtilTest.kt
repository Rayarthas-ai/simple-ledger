package com.arthas.simpleledger.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

class DateRangeUtilTest {
    private val zone = ZoneId.of("UTC")

    @Test
    fun week_usesMondayToNextMonday() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 8, 12), StatsPeriod.WEEK, zone)
        assertEquals(millis("2026-08-10"), range.startMillis)
        assertEquals(millis("2026-08-17"), range.endMillis)
    }

    @Test
    fun month_usesNaturalMonth() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 2, 28), StatsPeriod.MONTH, zone)
        assertEquals(millis("2026-02-01"), range.startMillis)
        assertEquals(millis("2026-03-01"), range.endMillis)
    }

    @Test
    fun quarter_handlesBoundary() {
        val q1 = DateRangeUtil.rangeFor(LocalDate.of(2026, 3, 31), StatsPeriod.QUARTER, zone)
        val q2 = DateRangeUtil.rangeFor(LocalDate.of(2026, 4, 1), StatsPeriod.QUARTER, zone)
        assertEquals("2026 Q1", q1.label)
        assertEquals(millis("2026-01-01"), q1.startMillis)
        assertEquals(millis("2026-04-01"), q1.endMillis)
        assertEquals("2026 Q2", q2.label)
        assertEquals(millis("2026-04-01"), q2.startMillis)
        assertEquals(millis("2026-07-01"), q2.endMillis)
    }

    @Test
    fun year_handlesYearBoundary() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 12, 31), StatsPeriod.YEAR, zone)
        assertEquals(millis("2026-01-01"), range.startMillis)
        assertEquals(millis("2027-01-01"), range.endMillis)
    }

    private fun millis(date: String): Long =
        Instant.parse("${date}T00:00:00Z").toEpochMilli()
}
