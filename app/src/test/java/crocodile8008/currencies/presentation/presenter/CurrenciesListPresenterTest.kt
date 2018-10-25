package crocodile8008.currencies.presentation.presenter

import com.nhaarman.mockitokotlin2.*
import crocodile8008.currencies.MockData.createShortBundle
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.data.model.CountryRate
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.presentation.view.CurrenciesAdapter
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import crocodile8008.currencies.utils.Exchanger
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesListPresenterTest {

    @Mock
    private lateinit var repo: CurrenciesRepo
    @Mock
    private lateinit var exchanger: Exchanger
    private val viewModel  = CurrenciesViewModel()

    private lateinit var presenter: CurrenciesListPresenter

    private val repoSubject = BehaviorSubject.createDefault<CurrenciesBundle>(createShortBundle())

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
        presenter = CurrenciesListPresenter(repo, exchanger, viewModel)
        whenever(repo.observeAllUpdates()).thenReturn(repoSubject)
    }

    @Test
    fun `WHEN view created and repo have data THEN fill view with data`() {
        val view = mock<CurrenciesView>()
        presenter.onViewCreated(view)
        verify(view).hideProgress()
        verify(view).showData(eq(listOf("EUR", "AUD", "BGN", "BRL")))
    }

    @Test
    fun `WHEN view resumed THEN start updates`() {
        presenter.onResume()
        verify(repo).startUpdates()
    }

    @Test
    fun `WHEN view paused THEN stop updates`() {
        presenter.onPause()
        verify(repo).stopUpdates()
    }

    @Test
    fun `WHEN view scrolled THEN hide keyboard`() {
        val view = mock<CurrenciesView>()
        presenter.onViewCreated(view)
        presenter.onScrolled()
        verify(view).hideKeyboard()
    }

    @Test
    fun `WHEN click on item THEN reorder data, scroll to top and show keyboard`() {
        val view = mock<CurrenciesView>()
        val itemView = mock<ItemView>()
        whenever(itemView.getDisplayData()).thenReturn(CountryRate("AUD", 1f))
        presenter.onViewCreated(view)
        verify(view).showData(eq(listOf("EUR", "AUD", "BGN", "BRL")))

        presenter.onClickItem(itemView)
        verify(view).showData(eq(listOf("AUD", "EUR", "BGN", "BRL")))
        verify(view).scrollToTop()
        verify(itemView).showKeyboard()
    }

    @Test
    fun `WHEN bind view and it's selected THEN update typed money on this view`() {
        val itemView = mock<CurrenciesAdapter.CurrencyViewHolder>()
        viewModel.selectedCountry = "EUR"
        viewModel.typedCount = 42f
        presenter.onBindItem(itemView, "EUR")
        Mockito.verify(exchanger, never()).exchange(any(), any(), any(), any())
        verify(itemView).setMoney("42.0")
    }

    @Test
    fun `WHEN bind view and it's not selected THEN update typed money on this view with conversion`() {
        val view = mock<CurrenciesView>()
        presenter.onViewCreated(view)
        val itemView = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("AUD", 1f)) }
        whenever(exchanger.exchange(any(), any(), any(), any())).thenReturn(85f)
        viewModel.selectedCountry = "EUR"
        viewModel.typedCount = 42f
        presenter.onBindItem(itemView, "AUD")
        Mockito.verify(exchanger).exchange(any(), eq("EUR"), eq(42f), eq("AUD"))
        verify(itemView).setMoney("85.0")
    }

    @Test
    fun `WHEN typed changed THEN update values only on secondary views`() {
        val view = mock<CurrenciesView>()
        val itemViewSelected = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("EUR", 1f)) }
        val itemView2 = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("AUD", 1f)) }
        val itemView3 = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("BGN", 1f)) }
        whenever(view.getAttachedItems()).thenReturn(setOf(itemViewSelected, itemView2, itemView3))
        viewModel.selectedCountry = "EUR"
        presenter.onViewCreated(view)

        presenter.onTypedChanges(itemViewSelected)
        verify(itemViewSelected, never()).setMoney(any())
        verify(itemView2).setMoney(any())
        verify(itemView3).setMoney(any())
        verify(exchanger, times(2)).exchange(any(), any(), any(), any())
    }

    @Test
    fun `WHEN rate changed THEN update values only on secondary views`() {
        val view = mock<CurrenciesView>()
        val itemViewSelected = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("EUR", 1f)) }
        val itemView2 = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("AUD", 1f)) }
        val itemView3 = mock<CurrenciesAdapter.CurrencyViewHolder>()
                .also { whenever(it.getDisplayData()).thenReturn(CountryRate("BGN", 1f)) }
        whenever(view.getAttachedItems()).thenReturn(setOf(itemViewSelected, itemView2, itemView3))
        viewModel.selectedCountry = "EUR"
        presenter.onViewCreated(view)

        repoSubject.onNext(createShortBundle())
        verify(itemViewSelected, never()).setMoney(any())
        verify(itemView2, times(2)).setMoney(any())
        verify(itemView3, times(2)).setMoney(any())
        verify(exchanger, times(4)).exchange(any(), any(), any(), any())
    }
}