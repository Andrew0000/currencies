package crocodile8008.currencies.presentation.view

import android.arch.lifecycle.ViewModel
import crocodile8008.currencies.data.model.CurrenciesBundle

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesViewModel : ViewModel() {
    companion object {
        const val NOTHING = -1f;
    }

    var selectedCountry = CurrenciesBundle.DEFAULT_COUNTRY
    var typedCount = 0f
    var displayCountWhenWasBeforeMainPosition = NOTHING
    var lastDisplayed : List<String> = ArrayList()
    var lastFullDisplayed : CurrenciesBundle = CurrenciesBundle.EMPTY

    fun isSelectedCountry(country : String) = country == selectedCountry
}