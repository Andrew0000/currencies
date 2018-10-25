package crocodile8008.currencies.presentation.viewmodel

import android.arch.lifecycle.ViewModel
import crocodile8008.currencies.data.model.CurrenciesBundle

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesViewModel : ViewModel() {

    var selectedCountry = CurrenciesBundle.DEFAULT_COUNTRY
    var lastDisplayedList : List<String> = ArrayList()
    var lastReceivedBundle : CurrenciesBundle = CurrenciesBundle.EMPTY
    var typedCount = 100f

    fun isSelectedCountry(country : String) = country == selectedCountry
}