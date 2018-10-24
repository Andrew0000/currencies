package crocodile8008.currencies.presentation.viewmodel

import android.arch.lifecycle.ViewModel
import crocodile8008.currencies.data.model.CurrenciesBundle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesViewModel : ViewModel() {
    companion object {
        const val NOTHING = -1f;
    }

    var selectedCountry = CurrenciesBundle.DEFAULT_COUNTRY
    var displayedMoneyBeforeSelectedPosition = NOTHING
    var lastDisplayedList : List<String> = ArrayList()
    var lastDisplayedFull : CurrenciesBundle = CurrenciesBundle.EMPTY
    private val typedCount = BehaviorSubject.createDefault(100f)

    fun observeTypedCount() : Observable<Float> = typedCount

    fun getTypedCount() : Float = typedCount.value

    fun setTypedCount(value : Float) {
        typedCount.onNext(value)
    }

    fun isSelectedCountry(country : String) = country == selectedCountry
}