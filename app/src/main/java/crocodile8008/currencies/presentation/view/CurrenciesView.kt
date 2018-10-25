package crocodile8008.currencies.presentation.view

/**
 * Created by Andrei Riik in 2018.
 */
interface CurrenciesView {

    fun showProgress()

    fun hideProgress()

    fun showData(data : List<String>)

    fun scrollToTop()

    fun hideKeyboard()

    fun getAttachedItems(): Set<ItemView>
}