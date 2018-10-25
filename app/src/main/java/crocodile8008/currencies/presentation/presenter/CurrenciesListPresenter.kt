package crocodile8008.currencies.presentation.presenter

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import crocodile8008.currencies.utils.Exchanger
import crocodile8008.currencies.utils.subscribeDisposable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesListPresenter @Inject constructor(
    private val repo: CurrenciesRepo,
    private val exchanger: Exchanger,
    private val viewModel : CurrenciesViewModel) {

    private lateinit var view : CurrenciesView

    private val disposable = CompositeDisposable()

    fun onViewCreated(view : CurrenciesView) {
        this.view = view
        repo.observeAllUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { view.showProgress() }
                .doOnNext {
                    viewModel.lastReceivedBundle = it
                    updateSecondaryCurrencies()
                }
                .map{ bundleToList(it) }
                .distinctUntilChanged()
                .subscribeDisposable(
                        { updateWholeList(it) },
                        disposable
                )
    }

    private fun bundleToList(bundle: CurrenciesBundle): List<String> =
        if (bundle.isEmpty()) {
            ArrayList()
        } else {
            ArrayList(bundle.rates.map { it.key }).apply { add(0, bundle.base) }
        }

    fun onBindItem(itemView: ItemView, country: String) {
        itemView.setCountry(country)
        if (viewModel.isSelectedCountry(country)) {
            itemView.setMoney(viewModel.typedCount.toString())
        } else {
            updateCurrencyOnNonSelectedItem(itemView, country)
        }
    }

    private fun updateCurrencyOnNonSelectedItem(itemView: ItemView, country: String) {
        if (viewModel.lastReceivedBundle.isEmpty()) {
            return
        }
        val exchanged = exchanger.exchange(
                viewModel.lastReceivedBundle, viewModel.selectedCountry, viewModel.typedCount, country)
        itemView.setMoney(exchanged.toString())
    }

    private fun updateSecondaryCurrencies() {
        view.getAttachedItems().forEach { itemView ->
            val country = itemView.getDisplayData().name
            if (!viewModel.isSelectedCountry(country)) {
                updateCurrencyOnNonSelectedItem(itemView, country)
            }
        }
        Lo.v("updateAllSecondaryCurrencies, all views: ${view.getAttachedItems().size}")
    }

    private fun updateWholeList(data: List<String>) {
        if (data.isEmpty()) {
            view.showProgress()
            return
        }
        reorderAndDisplay(data)
        view.hideProgress()
    }

    fun onClickItem(itemView : ItemView) {
        val data = itemView.getDisplayData()
        viewModel.typedCount = data.rate
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
            onClickItem(itemView)
        }
    }

    fun onTypedChanges(itemView : ItemView) {
        val data = itemView.getDisplayData()
        if (!itemView.hasPosition() || data.name.isEmpty()) {
            return
        }
        if (viewModel.isSelectedCountry(data.name)) {
            viewModel.typedCount = data.rate
            updateSecondaryCurrencies()
        }
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

    fun onScrolled() = view.hideKeyboard()

    fun onResume() = repo.startUpdates()

    fun onPause() = repo.stopUpdates()

    fun onDestroyView() = disposable.clear()
}