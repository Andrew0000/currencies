package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
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
    private val viewModel : CurrenciesViewModel) {

    private lateinit var view : CurrenciesView

    private val disposable = CompositeDisposable()

    fun onViewCreated(view : CurrenciesView) {
        this.view = view
        Lo.i("onViewCreated: $view")
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

    fun onResume() {
        Lo.i("onResume")
        repo.startUpdates()
    }

    private fun updateDisplayDataOnSuccess(data: List<String>) {
        if (data.isEmpty()) {
            view.showProgress()
            return
        }
        reorderAndDisplay(data)
        view.hideProgress()
    }

    fun onClickItem(itemView : ItemView) {
        val data = itemView.getDisplayData()
        Lo.i("onClickItem: $itemView, $data")
        viewModel.displayCountWhenWasBeforeMainPosition = data.rate
        viewModel.selectedCountry = data.name
        reorderAndDisplay(viewModel.lastDisplayedList)
        view.scrollToTop()
        itemView.showKeyboard()
    }

    fun onTextFocus(itemView : ItemView) {
        val data = itemView.getDisplayData()
        if (!itemView.hasPosition() || data.name.isEmpty()) {
            return
        }
        if (!viewModel.isSelectedCountry(data.name)) {
            Lo.d("onTextFocus: $itemView, $data")
            onClickItem(itemView)
        }
    }

    fun onTypedChanges(itemView : ItemView) {
        val data = itemView.getDisplayData()
        if (!itemView.hasPosition() || data.name.isEmpty()) {
            return
        }
        if (viewModel.isSelectedCountry(data.name)) {
            Lo.i("onTypedChanges: $itemView, $data")
            viewModel.setTypedCount(data.rate)
        }
    }

    fun onScrolled() {
        view.hideKeyboard()
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

    fun onPause() {
        Lo.i("onPause")
        repo.stopUpdates()
    }

    fun onDestroyView() {
        Lo.i("onDestroyView: $view")
        disposable.clear()
    }
}