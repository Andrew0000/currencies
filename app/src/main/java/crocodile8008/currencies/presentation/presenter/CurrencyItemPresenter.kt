package crocodile8008.currencies.presentation.presenter

import android.support.annotation.MainThread
import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import crocodile8008.currencies.utils.Exchanger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrencyItemPresenter @Inject constructor(
    private val repo: CurrenciesRepo,
    private val exchanger: Exchanger,
    private val viewModel : CurrenciesViewModel) {

    private val views = WeakHashMap<ItemView, String>()

    private var disposable : CompositeDisposable? = null

    @MainThread
    fun onBindView(itemView: ItemView, country: String) {
        observeDataIfNot()
        views[itemView] = country
        itemView.setCountry(country)
        if (viewModel.isSelectedCountry(country)) {
            updateCurrencyOnSelectedItem(itemView)
        } else {
            updateCurrencyOnNonSelectedItem(itemView, country)
        }
    }

    fun onViewRecycled(itemView: ItemView) {
        views.remove(itemView)
    }

    private fun updateCurrencyOnSelectedItem(itemView: ItemView) {
        itemView.setMoney(viewModel.getTypedCount().toString())
    }

    @MainThread
    private fun observeDataIfNot() {
        if (disposable != null) {
            return
        }
        disposable = CompositeDisposable().apply {
            add(observeRepo())
            add(observeTypedCount())
        }
    }

    private fun observeRepo() =
            repo.observeAllUpdates()
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { viewModel.lastDisplayedFull = it }
                    .subscribe(
                            { updateAllSecondaryCurrencies() },
                            { Lo.e("", it) }
                    )

    private fun observeTypedCount() =
            viewModel.observeTypedCount()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { updateAllSecondaryCurrencies() },
                            { Lo.e("", it) }
                    )

    @MainThread
    private fun updateAllSecondaryCurrencies() {
        views.forEach { (itemView, country) ->
            if (!viewModel.isSelectedCountry(country)) {
                updateCurrencyOnNonSelectedItem(itemView, country)
            }
        }
        Lo.v("updateAllSecondaryCurrencies, all views: ${views.size}")
    }

    private fun updateCurrencyOnNonSelectedItem(itemView: ItemView, country: String) {
        if (viewModel.lastDisplayedFull.isEmpty()) {
            return
        }
        val exchanged = exchanger.exchange(
                viewModel.lastDisplayedFull, viewModel.selectedCountry, viewModel.getTypedCount(), country)
        itemView.setMoney(exchanged.toString())
    }

    @MainThread
    fun onDestroyView() {
        views.clear()
        disposable?.dispose()
    }
}