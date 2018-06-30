package crocodile8008.currencies.utils

import crocodile8008.currencies.data.CurrenciesBundle
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Created by Andrei Riik in 2018.
 */
class ExchangerTest {

    private val currencies = CurrenciesBundle(
            "EUR",
            "12345",
            mapOf(
                    Pair("AUD", 1.5f),
                    Pair("BGN", 2f),
                    Pair("BRL", 1.1f),
                    Pair("CAD", 0.5f),
                    Pair("CHF", 1f),
                    Pair("DKK", 1.5f)
            )
    )

    private val exchanger = Exchanger()

    //==================================================================================================================

    @Test
    fun eur_1_to_aud() {
        assertEquals(1.5f, exchange("EUR", 1f, "AUD"))
    }

    @Test
    fun eur_2_5_to_aud() {
        assertEquals(3.75f, exchange("EUR", 2.5f, "AUD"))
    }

    @Test
    fun eur_2_5_to_chf() {
        assertEquals(2.5f, exchange("EUR", 2.5f, "CHF"))
    }

    @Test
    fun eur_2_5_to_cad() {
        assertEquals(1.25f, exchange("EUR", 2.5f, "CAD"))
    }

    //==================================================================================================================

    @Test
    fun aud_1_5_to_eur() {
        Assert.assertEquals(1f, exchange("AUD", 1.5f, "EUR"))
    }

    @Test
    fun aud_3_75_to_eur() {
        assertEquals(2.5f, exchange("AUD", 3.75f, "EUR"))
    }

    @Test
    fun chf_2_5_to_eur() {
        assertEquals(2.5f, exchange("CHF", 2.5f, "EUR"))
    }

    @Test
    fun cad_1_75_to_eur() {
        assertEquals(2.5f, exchange("CAD", 1.25f, "EUR"))
    }

    //==================================================================================================================

    @Test
    fun aud_1_5_to_dkk() {
        Assert.assertEquals(1.5f, exchange("AUD", 1.5f, "DKK"))
    }

    @Test
    fun aud_1_5_to_cad() {
        Assert.assertEquals(0.5f, exchange("AUD", 1.5f, "CAD"))
    }

    @Test
    fun bgn_1_5_to_chf() {
        Assert.assertEquals(0.75f, exchange("BGN", 1.5f, "CHF"))
    }

    //==================================================================================================================

    @Test
    fun dkk_10_to_aud() {
        Assert.assertEquals(10f, exchange("DKK", 10f, "AUD"))
    }

    @Test
    fun cad_10_to_aud() {
        Assert.assertEquals(30f, exchange("CAD", 10f, "AUD"))
    }

    @Test
    fun chf_10_to_bgn() {
        Assert.assertEquals(20f, exchange("CHF", 10f, "BGN"))
    }

    //==================================================================================================================

    private fun exchange(fromCountry : String,
                         fromCount : Float,
                         toCountry : String) = exchanger.exchange(currencies, fromCountry, fromCount, toCountry)
}