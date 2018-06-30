package crocodile8008.currencies.utils

import crocodile8008.currencies.data.model.CurrenciesBundle
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class Exchanger @Inject constructor() {

    /**
     * See ExchangerTest
     */
    fun exchange(currencies : CurrenciesBundle,
                 fromCountry : String,
                 fromCount : Float,
                 toCountry : String) : Float {

        if (fromCount < 0 || fromCountry.isEmpty() || toCountry.isEmpty()) {
            return 0f
        }
        if (fromCountry == currencies.base) {
            return (currencies.rates[toCountry] ?: 0f) * fromCount
        }
        val toBaseRate = currencies.rates[fromCountry] ?: 0f
        val baseCount = fromCount / toBaseRate
        if (toCountry == currencies.base) {
            return baseCount
        }
        return (currencies.rates[toCountry] ?: 0f) * baseCount
    }
}