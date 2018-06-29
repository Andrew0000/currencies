package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesBundle
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.CurrenciesViewModel
import crocodile8008.currencies.utils.subscribeAndAddToDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesPresenter @Inject constructor(
    private val repo: CurrenciesRepo,
    private val viewModel : CurrenciesViewModel) {

    private lateinit var view : CurrenciesView

    private val disposable = CompositeDisposable()

    fun onViewCreated(view : CurrenciesView) {
        this.view = view
        Lo.i("onViewCreated: $view")
        repo.startUpdates()
        repo.observeAllUpdates()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showProgress() }
                .subscribeAndAddToDisposable(
                        { updateDisplayDataOnSuccess(it) },
                        { Lo.e("error", it) },
                        disposable
                )
    }

    private fun updateDisplayDataOnSuccess(data: CurrenciesBundle) {
        val list = data.rates.map { mapEntry -> Pair(mapEntry.key, mapEntry.value) }
        reorderAndDisplay(list)
        view.hideProgress()
    }

    fun onClickItem(item : Pair<String, Float>) {
        Lo.i("onClickItem: $item")
        viewModel.selectedCountry = item.first
        reorderAndDisplay(viewModel.lastDisplayed)
        view.scrollToTop()
    }

    private fun reorderAndDisplay(list : List<Pair<String, Float>>) {
        val reordered = reorderAccordingSelected(list)
        view.showData(reordered)
        viewModel.lastDisplayed = reordered
    }

    private fun reorderAccordingSelected(list : List<Pair<String, Float>>) : List<Pair<String, Float>> {
        if (viewModel.selectedCountry.isEmpty()) {
            return list
        }
        var foundedItem : Pair<String, Float>? = null
        for (item in list) {
            if (item.first == viewModel.selectedCountry) {
                foundedItem = item
                break
            }
        }
        if (foundedItem != null) {
            val result = ArrayList<Pair<String, Float>>(list)
            result.remove(foundedItem)
            result.add(0, foundedItem)
            return result
        }
        return list
    }

    fun onDestroyView() {
        Lo.i("onDestroyView: $view")
        repo.stopUpdates()
        disposable.clear()
    }
}