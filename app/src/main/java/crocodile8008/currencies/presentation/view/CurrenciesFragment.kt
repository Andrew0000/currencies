package crocodile8008.currencies.presentation.view

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import crocodile8008.common.log.Lo
import crocodile8008.currencies.App
import crocodile8008.currencies.R
import crocodile8008.currencies.presentation.presenter.CurrenciesPresenter
import kotlinx.android.synthetic.main.currencies_fragment.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesFragment : Fragment(), CurrenciesView {

    @Inject lateinit var presenter : CurrenciesPresenter
    @Inject lateinit var adapter : CurrenciesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.instance.getMainActivityComponent().inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.currencies_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
        recycler.setHasFixedSize(true)
        adapter.observeClicks().subscribe { presenter.onClickItem(it) }
        presenter.onViewCreated(this)
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun showData(data : List<Pair<String, Float>>) {
        Lo.d("showData: $data")
        recycler.scrollToPosition(0)
        adapter.update(data)
    }

    override fun onDestroyView() {
        presenter.onDestroyView()
        super.onDestroyView()
    }
}