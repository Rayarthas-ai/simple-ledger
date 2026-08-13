package com.arthas.simpleledger.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MoneyFormatterTest {
    @Test
    fun parseToMinor_convertsDecimalMoneyExactly() {
        assertEquals(15050L, MoneyFormatter.parseToMinor("150.50"))
        assertEquals(50000L, MoneyFormatter.parseToMinor("500"))
        assertEquals(1L, MoneyFormatter.parseToMinor("0.01"))
    }

    @Test
    fun parseToMinor_rejectsInvalidAmounts() {
        assertNull(MoneyFormatter.parseToMinor(""))
        assertNull(MoneyFormatter.parseToMinor("0"))
        assertNull(MoneyFormatter.parseToMinor("-1"))
        assertNull(MoneyFormatter.parseToMinor("1.234"))
        assertNull(MoneyFormatter.parseToMinor("abc"))
    }
}
