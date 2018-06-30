package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.CurrenciesViewModel
import crocodile8008.currencies.utils.subscribeAndAddToDisposable
import io.reactivex.Observable
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
                .flatMap{ Observable.just(it.rates.map { it.key }) }
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showProgress() }
                .subscribeAndAddToDisposable(
                        { updateDisplayDataOnSuccess(it) },
                        { Lo.e("error", it) },
                        disposable
                )
    }

    private fun updateDisplayDataOnSuccess(data: List<String>) {
        if (data.isEmpty()) {
            view.showProgress()
            return
        }
        reorderAndDisplay(data)
        view.hideProgress()
    }

    fun onClickItem(item : String) {
        Lo.i("onClickItem: $item")
        viewModel.selectedCountry = item
        reorderAndDisplay(viewModel.lastDisplayed)
        view.scrollToTop()
    }

    private fun reorderAndDisplay(list : List<String>) {
        val reordered = reorderAccordingSelected(list)
        view.showData(reordered)
        viewModel.lastDisplayed = reordered
    }

    private fun reorderAccordingSelected(list : List<String>) : List<String> {
        if (viewModel.selectedCountry.isEmpty() || !list.contains(viewModel.selectedCountry)) {
            return list
        }
        val result = ArrayList<String>(list)
        result.remove(viewModel.selectedCountry)
        result.add(0, viewModel.selectedCountry)
        return result
    }

    fun onDestroyView() {
        Lo.i("onDestroyView: $view")
        repo.stopUpdates()
        disposable.clear()
    }
}