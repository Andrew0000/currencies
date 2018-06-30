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
import crocodile8008.currencies.presentation.presenter.CurrenciesListPresenter
import kotlinx.android.synthetic.main.currencies_fragment.*
import javax.inject.Inject

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesFragment : Fragment(), CurrenciesView {

    @Inject lateinit var presenter : CurrenciesListPresenter
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
        adapter.observeTextFocus().subscribe { presenter.onTextFocus(it) }
        adapter.observeTypedMoney().subscribe { presenter.onTypedChanges(it) }
        presenter.onViewCreated(this)
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.INVISIBLE
    }

    override fun showData(data : List<String>) {
        Lo.d("showData: $data")
        adapter.update(data)
    }

    override fun scrollToTop() {
        recycler.scrollToPosition(0)
    }

    override fun onDestroyView() {
        presenter.onDestroyView()
        adapter.onDestroyView()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.refWatcher.watch(this)
    }
}