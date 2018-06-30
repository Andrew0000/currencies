package crocodile8008.currencies.presentation.presenter

import android.support.annotation.MainThread
import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.CurrenciesBundle
import crocodile8008.currencies.data.CurrenciesRepo
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
class CurrencyItemsPresenter @Inject constructor(
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
        if (!last.isEmpty()) {
            val baseCountry = if (viewModel.selectedCountry.isEmpty()) last.base else viewModel.selectedCountry
            val exchanged = exchanger.exchange(last, baseCountry, 1f, country)
            holder.money.setText(exchanged.toString())
        }
    }

    private fun observeRepoIfNot() {
        if (disposable == null) {
            disposable = repo.observeAllUpdates()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            {
                                last = it;
                                val baseCountry = if (viewModel.selectedCountry.isEmpty()) it.base else viewModel.selectedCountry
                                holders.forEach { (holder, country) ->
                                    val exchanged = exchanger.exchange(it, baseCountry, 1f, country)
                                    holder.money.setText(exchanged.toString())
                                }
                            },
                            { Lo.e("", it) }
                    )
        }
    }

    @MainThread
    fun onDestroyView() {
        holders.clear()
        disposable?.dispose()
    }
}