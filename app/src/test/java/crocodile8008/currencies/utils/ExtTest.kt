package crocodile8008.currencies.utils

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Andrei Riik in 2018.
 */
class ExtTest {

    @Test
    fun format2Digits1() {
        assertEquals("12,13", 12.1345f.format2Digits())
    }

    @Test
    fun format2Digits2() {
        assertEquals("12345,13", 12345.1345f.format2Digits())
    }

    @Test
    fun format2Digits3() {
        assertEquals("12345,50", 12345.5f.format2Digits())
    }

    @Test
    fun format2Digits4() {
        assertEquals("-12345,50", (-12345.5f).format2Digits())
    }

    @Test
    fun toFloatOrZero1() {
        assertEquals(12345.5f, "12345,50".toFloatOrZero())
    }

    @Test
    fun toFloatOrZero2() {
        assertEquals(12345.5f, "12345.500".toFloatOrZero())
    }


    @Test
    fun toFloatOrZero3() {
        assertEquals(-12345.5f, "-12345.500".toFloatOrZero())
    }

    @Test
    fun toFloatOrZero4() {
        assertEquals(123f, "123".toFloatOrZero())
    }
}