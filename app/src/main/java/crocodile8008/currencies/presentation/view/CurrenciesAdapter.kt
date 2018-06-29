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
    private val inflater : LayoutInflater) : RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    private val values = ArrayList<Pair<String, Float>>()
    private val clickSubject = PublishSubject.create<Pair<String, Float>>()
    private val typedSubject = PublishSubject.create<String>()

    private val textWatcher = object : EmptyTextWatcher() {
        override fun afterTextChanged(s: Editable?) {
            if (s == null) {
                return
            }
            typedSubject.onNext(s.toString())
        }
    }

    @MainThread
    fun update(newValues : List<Pair<String, Float>>) {
        val diffCallback = DiffCallback(ArrayList(values), ArrayList(newValues))
        values.clear()
        values.addAll(newValues)
        val diffResult = DiffUtil.calculateDiff(diffCallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun observeClicks() : Observable<Pair<String, Float>> = clickSubject

    fun observeTypedMoney() : Observable<String> = typedSubject

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder(inflater.inflate(R.layout.currency_item, parent, false))
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val item = values[position]
        holder.country.text = item.first
        holder.itemView.setOnClickListener {
            holder.money.showKeyboard()
            clickSubject.onNext(item)
        }
        if (position == 0) {
            holder.money.addTextChangedListener(textWatcher)
            holder.money.setOnFocusChangeListener(null)
        } else {
            holder.money.removeTextChangedListener(textWatcher)
            holder.money.setOnFocusChangeListener { view, focused ->
                if (focused) {
                    holder.itemView.performClick()
                }
            }
        }
    }

    override fun getItemCount() = values.size

    inner class CurrencyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val country: TextView = view.countryCodeTextView
        val money: EditText = view.money
    }
}