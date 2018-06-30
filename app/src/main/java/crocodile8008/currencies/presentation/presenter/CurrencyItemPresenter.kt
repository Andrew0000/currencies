package crocodile8008.currencies.presentation.presenter

import android.support.annotation.MainThread
import android.support.v7.widget.RecyclerView
import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import crocodile8008.currencies.utils.Exchanger
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
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

    private var disposable : Disposable? = null

    @MainThread
    fun onBindViewHolder(holder: T, country: String) {
        observeRepoIfNot()
        holders[holder] = country
        holder.setCountry(country)
        if (viewModel.lastDisplayedFull.isEmpty()) {
            return
        }
        if (viewModel.isSelectedCountry(country)) {
            updateCurrencyOnBaseItem(holder)
        } else {
            updateCurrencyOnItem(holder, country)
        }
    }

    private fun updateCurrencyOnBaseItem(holder: T) {
        if (viewModel.displayCountWhenWasBeforeMainPosition != CurrenciesViewModel.NOTHING) {
            holder.setMoney(viewModel.displayCountWhenWasBeforeMainPosition.toString())
            viewModel.displayCountWhenWasBeforeMainPosition = CurrenciesViewModel.NOTHING
        } else {
            holder.setMoney(viewModel.typedCount.toString())
        }
    }

    private fun observeRepoIfNot() {
        if (disposable != null) {
            return
        }
        disposable = repo.observeAllUpdates()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        {
                            viewModel.lastDisplayedFull = it
                            holders.forEach { (holder, country) ->
                                if (!viewModel.isSelectedCountry(country)) {
                                    updateCurrencyOnItem(holder, country)
                                }
                            }
                        },
                        { Lo.e("", it) }
                )
    }

    private fun updateCurrencyOnItem(holder: T, country: String) {
        val exchanged = exchanger.exchange(
                viewModel.lastDisplayedFull, viewModel.selectedCountry, viewModel.typedCount, country)
        holder.setMoney(exchanged.toString())
    }

    @MainThread
    fun onDestroyView() {
        holders.clear()
        disposable?.dispose()
    }
}