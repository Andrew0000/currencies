package crocodile8008.currencies.presentation.presenter

import android.support.annotation.MainThread
import android.support.v7.widget.RecyclerView
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
class CurrencyItemPresenter<T> @Inject constructor(
    private val repo: CurrenciesRepo,
    private val exchanger: Exchanger,
    private val viewModel : CurrenciesViewModel) where T : RecyclerView.ViewHolder, T : ItemView {

    private val holders = WeakHashMap<T, String>()

    private var disposable : CompositeDisposable? = null

    @MainThread
    fun onBindViewHolder(holder: T, country: String) {
        observeDataIfNot()
        holders[holder] = country
        holder.setCountry(country)
        if (viewModel.isSelectedCountry(country)) {
            updateCurrencyOnBaseItem(holder)
        } else if (!viewModel.lastDisplayedFull.isEmpty()) {
            updateCurrencyOnItem(holder, country)
        }
    }

    fun onViewRecycled(holder: T) {
        holders.remove(holder)
    }

    private fun updateCurrencyOnBaseItem(holder: T) {
        if (viewModel.displayCountWhenWasBeforeMainPosition != CurrenciesViewModel.NOTHING) {
            holder.setMoney(viewModel.displayCountWhenWasBeforeMainPosition.toString())
            viewModel.displayCountWhenWasBeforeMainPosition = CurrenciesViewModel.NOTHING
        } else {
            holder.setMoney(viewModel.getTypedCount().toString())
        }
    }

    @MainThread
    private fun observeDataIfNot() {
        if (disposable != null) {
            return
        }
        val tmp = CompositeDisposable()
        tmp.add(observeRepo())
        tmp.add(observeTypedCount())
        disposable = tmp
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
        holders.forEach { (holder, country) ->
            if (!viewModel.isSelectedCountry(country)) {
                updateCurrencyOnItem(holder, country)
            }
        }
        Lo.v("updateAllSecondaryCurrencies, all holders: ${holders.size}")
    }

    private fun updateCurrencyOnItem(holder: T, country: String) {
        val exchanged = exchanger.exchange(
                viewModel.lastDisplayedFull, viewModel.selectedCountry, viewModel.getTypedCount(), country)
        holder.setMoney(exchanged.toString())
    }

    @MainThread
    fun onDestroyView() {
        holders.clear()
        disposable?.dispose()
    }
}