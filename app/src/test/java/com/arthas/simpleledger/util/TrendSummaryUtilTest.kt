package com.arthas.simpleledger.util

import com.arthas.simpleledger.data.DailyExpenseSummary
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class TrendSummaryUtilTest {
    private val zone = ZoneId.of("UTC")

    @Test
    fun fillTrend_addsZeroDaysBetweenExistingRows() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 8, 1), StatsPeriod.MONTH, zone)
        val points = TrendSummaryUtil.fillTrend(
            StatsPeriod.MONTH,
            range,
            listOf(
                DailyExpenseSummary("2026-08-01", 35000),
                DailyExpenseSummary("2026-08-03", 50000)
            ),
            zone
        )

        assertEquals("2026-08-01", points[0].key)
        assertEquals(35000L, points[0].amountMinor)
        assertEquals("2026-08-02", points[1].key)
        assertEquals(0L, points[1].amountMinor)
        assertEquals("2026-08-03", points[2].key)
        assertEquals(50000L, points[2].amountMinor)
    }

    @Test
    fun fillTrend_february2026Has28Days() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 2, 15), StatsPeriod.MONTH, zone)
        val points = TrendSummaryUtil.fillTrend(StatsPeriod.MONTH, range, emptyList(), zone)

        assertEquals(28, points.size)
        assertEquals("2026-02-01", points.first().key)
        assertEquals("2026-02-28", points.last().key)
    }

    @Test
    fun fillTrend_leapFebruary2028Has29Days() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2028, 2, 15), StatsPeriod.MONTH, zone)
        val points = TrendSummaryUtil.fillTrend(StatsPeriod.MONTH, range, emptyList(), zone)

        assertEquals(29, points.size)
        assertEquals("2028-02-29", points.last().key)
    }

    @Test
    fun quarterBoundariesStayInTheirNaturalQuarters() {
        val q1 = DateRangeUtil.rangeFor(LocalDate.of(2026, 3, 31), StatsPeriod.QUARTER, zone)
        val q2 = DateRangeUtil.rangeFor(LocalDate.of(2026, 4, 1), StatsPeriod.QUARTER, zone)

        assertEquals("2026 Q1", q1.label)
        assertEquals("2026 Q2", q2.label)
    }

    @Test
    fun yearTrendAlwaysHas12MonthlyPoints() {
        val range = DateRangeUtil.rangeFor(LocalDate.of(2026, 8, 12), StatsPeriod.YEAR, zone)
        val points = TrendSummaryUtil.fillTrend(
            StatsPeriod.YEAR,
            range,
            listOf(DailyExpenseSummary("2026-08", 120000)),
            zone
        )

        assertEquals(12, points.size)
        assertEquals("2026-01", points.first().key)
        assertEquals("2026-12", points.last().key)
        assertEquals(120000L, points[7].amountMinor)
    }
}
