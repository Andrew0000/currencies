package crocodile8008.currencies.presentation.view

import android.arch.lifecycle.ViewModel

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesViewModel : ViewModel() {

    var selectedCountry = ""
    var lastDisplayed : List<String> = ArrayList()
}