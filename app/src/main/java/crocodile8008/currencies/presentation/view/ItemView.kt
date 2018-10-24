package crocodile8008.currencies.presentation.view

import crocodile8008.currencies.data.model.CountryRate

/**
 * Created by Andrei Riik in 2018.
 */
interface ItemView {

    fun setCountry(text : String)

    fun setMoney(text : String)

    fun showKeyboard()

    fun getDisplayData(): CountryRate

    fun hasPosition(): Boolean
}