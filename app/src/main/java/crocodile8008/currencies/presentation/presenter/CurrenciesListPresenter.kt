package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.MainActivity
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.CurrenciesAdapter
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import crocodile8008.currencies.utils.Utils
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
class CurrenciesListPresenter @Inject constructor(
    private val repo: CurrenciesRepo,
    private val viewModel : CurrenciesViewModel,
    private val activity : MainActivity) {

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
        reorderAndDisplay(viewModel.lastDisplayedList)
        view.scrollToTop()
        holder.showKeyboard()
    }

    fun isBasePosition(holder : CurrenciesAdapter.CurrencyViewHolder) : Boolean {
        val item = holder.getDisplayData()
        if (holder.hasNoPosition() || item.name.isEmpty() || viewModel.isSelectedCountry(item.name)) {
            return false
        }
        return true
    }

    fun onTextFocus(holder : CurrenciesAdapter.CurrencyViewHolder) {
        val item = holder.getDisplayData()
        Lo.d("onTextFocus: $holder, $item")
        onClickItem(holder)
    }

    fun onTypedChanges(holder : CurrenciesAdapter.CurrencyViewHolder) {
        val item = holder.getDisplayData()
        if (holder.hasNoPosition() || item.name.isEmpty() || !viewModel.isSelectedCountry(item.name)) {
            return
        }
        Lo.i("onTypedChanges: $holder, $item")
        viewModel.typedCount = item.rate
    }

    fun onScrolled() {
        Utils.hideKeyboard(activity)
    }

    private fun reorderAndDisplay(list : List<String>) {
        val reordered = reorderAccordingSelected(list)
        view.showData(reordered)
        viewModel.lastDisplayedList = reordered
    }

    private fun reorderAccordingSelected(list : List<String>) : List<String> {
        val selected = viewModel.selectedCountry
        if (selected.isEmpty() || !list.contains(selected)) {
            return list
        }
        val result = ArrayList<String>(list)
        result.remove(selected)
        result.add(0, selected)
        return result
    }

    fun onDestroyView() {
        Lo.i("onDestroyView: $view")
        repo.stopUpdates()
        disposable.clear()
    }
}