package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.CurrenciesAdapter
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.CurrenciesViewModel
import crocodile8008.currencies.utils.hasNoPosition
import crocodile8008.currencies.utils.subscribeAndAddToDisposable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
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
                .flatMap{ Observable.just(
                                if (it.isEmpty()) {
                                    ArrayList()
                                } else {
                                    ArrayList(it.rates.map { it.key }).apply { add(0, it.base) }
                                }
                )
                }
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

    fun onClickItem(holder : CurrenciesAdapter.CurrencyViewHolder) {
        val data = holder.getDisplayData()
        Lo.i("onClickItem: $holder, $data")
        viewModel.displayCountWhenWasBeforeMainPosition = data.rate
        viewModel.selectedCountry = data.name
        reorderAndDisplay(viewModel.lastDisplayed)
        view.scrollToTop()
        holder.showKeyboard()
    }

    fun onTextFocus(holder : CurrenciesAdapter.CurrencyViewHolder) {
        val item = holder.getDisplayData()
        if (holder.hasNoPosition() || item.name.isEmpty() || viewModel.selectedCountry == item.name) {
            return
        }
        Lo.d("onTextFocus: $holder, $item")
        onClickItem(holder)
    }

    fun onTypedChanges(holder : CurrenciesAdapter.CurrencyViewHolder) {
        val item = holder.getDisplayData()
        if (holder.hasNoPosition() || item.name.isEmpty() || viewModel.selectedCountry != item.name) {
            return
        }
        Lo.i("onTypedChanges: $holder, $item")
        viewModel.typedCount = item.rate
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