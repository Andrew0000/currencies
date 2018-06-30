package crocodile8008.currencies.presentation.viewmodel

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
    var lastDisplayedList : List<String> = ArrayList()
    var lastDisplayedFull : CurrenciesBundle = CurrenciesBundle.EMPTY

    fun isSelectedCountry(country : String) = country == selectedCountry
}