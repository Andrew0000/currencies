package crocodile8008.currencies.presentation.view

import android.arch.lifecycle.ViewModel
import crocodile8008.currencies.data.model.CurrenciesBundle

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesViewModel : ViewModel() {

    var selectedCountry = CurrenciesBundle.DEFAULT_COUNTRY
    var typedCount = 0f
    var displayCountWhenWasBeforeMainPosition = 0f
    var lastDisplayed : List<String> = ArrayList()

    fun isSelectedCountry(country : String) = country == selectedCountry
}