package crocodile8008.currencies

import crocodile8008.currencies.data.model.CurrenciesBundle

/**
 * Created by Andrei Riik in 2018.
 */
object MockData {

    fun createShortBundle() = CurrenciesBundle(
            "EUR",
            "12345",
            mapOf(
                    Pair("AUD", 1.5f),
                    Pair("BGN", 2f),
                    Pair("BRL", 1.1f)
            )
    )
}