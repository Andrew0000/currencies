package crocodile8008.currencies.presentation.presenter

import android.support.annotation.MainThread
import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.presentation.view.CurrenciesAdapter
import crocodile8008.currencies.presentation.view.CurrenciesViewModel
import crocodile8008.currencies.utils.Exchanger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrencyItemPresenter @Inject constructor(
    private val repo: CurrenciesRepo,
    private val exchanger: Exchanger,
    private val viewModel : CurrenciesViewModel) {

    private val holders = WeakHashMap<CurrenciesAdapter.CurrencyViewHolder, String>()
    private var last : CurrenciesBundle = CurrenciesBundle.EMPTY

    private var disposable : Disposable? = null

    @MainThread
    fun onBindViewHolder(holder: CurrenciesAdapter.CurrencyViewHolder, country: String) {
        observeRepoIfNot()
        holders[holder] = country
        holder.setCountry(country)
        if (last.isEmpty()) {
            return
        }
        if (viewModel.isSelectedCountry(country)) {
            holder.setMoney(viewModel.displayCountWhenWasBeforeMainPosition.toString())
        } else {
            updateCurrencyOnItem(holder, country)
        }
    }

    private fun observeRepoIfNot() {
        if (disposable == null) {
            disposable = repo.observeAllUpdates()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                last = it
                                holders.forEach { (holder, country) ->
                                    if (!viewModel.isSelectedCountry(country)) {
                                        updateCurrencyOnItem(holder, country)
                                    }
                                }
                            },
                            { Lo.e("", it) }
                    )
        }
    }

    private fun updateCurrencyOnItem(holder: CurrenciesAdapter.CurrencyViewHolder, country: String) {
        val exchanged = exchanger.exchange(last, viewModel.selectedCountry, viewModel.typedCount, country)
        holder.setMoney(exchanged.toString())
    }

    @MainThread
    fun onDestroyView() {
        holders.clear()
        disposable?.dispose()
    }
}