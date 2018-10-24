package crocodile8008.currencies.presentation.view

import android.support.annotation.MainThread
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import crocodile8008.currencies.R
import crocodile8008.currencies.data.model.CountryRate
import crocodile8008.currencies.presentation.presenter.CurrencyItemPresenter
import crocodile8008.currencies.utils.EmptyTextWatcher
import crocodile8008.currencies.utils.showKeyboard
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.currency_item.view.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesAdapter @Inject constructor(
    private val inflater : LayoutInflater,
    private val currencyPresenter: CurrencyItemPresenter<CurrenciesAdapter.CurrencyViewHolder>
) : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    private val values = ArrayList<String>()
    private val clickSubject = PublishSubject.create<ItemView>()
    private val focusSubject = PublishSubject.create<ItemView>()
    private val typedSubject = PublishSubject.create<ItemView>()

    fun observeClicks() : Observable<ItemView> = clickSubject

    fun observeTextFocus() : Observable<ItemView> = focusSubject

    fun observeTypedMoney() : Observable<ItemView> = typedSubject

    @MainThread
    fun update(newValues : List<String>) {
        val diffCallback = DiffCallback(ArrayList(values), ArrayList(newValues))
        values.clear()
        values.addAll(newValues)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(inflater.inflate(R.layout.currency_item, parent, false))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        currencyPresenter.onBindViewHolder(holder, values[position])
    }

    override fun onViewRecycled(holder: CurrencyViewHolder) {
        super.onViewRecycled(holder)
        currencyPresenter.onViewRecycled(holder)
    }

    fun onDestroyView() {
        currencyPresenter.onDestroyView()
    }

    override fun getItemCount() = values.size

    inner class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view), ItemView {

        private val country: TextView = view.countryCodeTextView
        private val money: EditText = view.money

        init {
            itemView.setOnClickListener {
                clickSubject.onNext(this)
            }
            money.post{
                money.setOnFocusChangeListener { _, focused ->
                    if (focused) {
                        focusSubject.onNext(this)
                    }
                }
            }
            money.addTextChangedListener(object : EmptyTextWatcher() {
                override fun afterTextChanged(s: Editable?) {
                    typedSubject.onNext(this@CurrencyViewHolder)
                }
            })
        }

        override fun setCountry(text : String) {
            country.text = text
        }

        override fun setMoney(text : String) {
            money.setText(text)
        }

        override fun showKeyboard() {
            money.showKeyboard()
        }

        override fun getDisplayData() = CountryRate.parse(country.text.toString(), money.text.toString())

        override fun hasPosition() = this.adapterPosition != RecyclerView.NO_POSITION
    }
}