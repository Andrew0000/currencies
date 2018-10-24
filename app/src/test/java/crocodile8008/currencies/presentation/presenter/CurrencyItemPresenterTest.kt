package crocodile8008.currencies.presentation.presenter

import com.nhaarman.mockitokotlin2.*
import crocodile8008.currencies.MockData.createShortBundle
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.presentation.view.CurrenciesAdapter
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

class CurrencyItemPresenterTest {

    @Mock
    private lateinit var repo: CurrenciesRepo
    @Mock
    private lateinit var exchanger: Exchanger
    private val viewModel  = CurrenciesViewModel()

    private val repoSubject = BehaviorSubject.createDefault<CurrenciesBundle>(createShortBundle())

    private lateinit var presenter: CurrencyItemPresenter

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
        presenter = CurrencyItemPresenter(repo, exchanger, viewModel)
        whenever(repo.observeAllUpdates()).thenReturn(repoSubject)
    }

    @Test
    fun `WHEN bind view and it's selected THEN update typed money on this view`() {
        val itemView = mock<CurrenciesAdapter.CurrencyViewHolder>()
        viewModel.selectedCountry = "EUR"
        viewModel.setTypedCount(42f)
        presenter.onBindView(itemView, "EUR")
        Mockito.verify(exchanger, never()).exchange(any(), any(), any(), any())
        verify(itemView).setMoney("42.0")
    }

    @Test
    fun `WHEN bind view and it's not selected THEN update typed money on this view with conversion`() {
        val itemView = mock<CurrenciesAdapter.CurrencyViewHolder>()
        whenever(exchanger.exchange(any(), any(), any(), any())).thenReturn(85f)
        viewModel.selectedCountry = "EUR"
        viewModel.setTypedCount(42f)
        presenter.onBindView(itemView, "AUD")
        Mockito.verify(exchanger).exchange(any(), eq("EUR"), eq(42f), eq("AUD"))
        verify(itemView).setMoney("85.0")
    }

    @Test
    fun `WHEN typed changed THEN update values only on secondary views`() {
        val itemViewSelected = mock<CurrenciesAdapter.CurrencyViewHolder>()
        val itemView2 = mock<CurrenciesAdapter.CurrencyViewHolder>()
        val itemView3 = mock<CurrenciesAdapter.CurrencyViewHolder>()
        viewModel.selectedCountry = "EUR"
        presenter.onBindView(itemViewSelected, "EUR")
        presenter.onBindView(itemView2, "AUD")
        presenter.onBindView(itemView3, "BGN")
        verify(itemViewSelected).setMoney(any())
        verify(itemView2).setMoney(any())
        verify(itemView3).setMoney(any())
        verify(exchanger, times(2)).exchange(any(), any(), any(), any())
        reset(itemViewSelected, itemView2, itemView3, exchanger)

        viewModel.setTypedCount(42f)
        verify(itemViewSelected, never()).setMoney(any())
        verify(itemView2).setMoney(any())
        verify(itemView3).setMoney(any())
        verify(exchanger, times(2)).exchange(any(), any(), any(), any())
    }

    @Test
    fun `WHEN rate changed THEN update values only on secondary views`() {
        val itemViewSelected = mock<CurrenciesAdapter.CurrencyViewHolder>()
        val itemView2 = mock<CurrenciesAdapter.CurrencyViewHolder>()
        val itemView3 = mock<CurrenciesAdapter.CurrencyViewHolder>()
        viewModel.selectedCountry = "EUR"
        presenter.onBindView(itemViewSelected, "EUR")
        presenter.onBindView(itemView2, "AUD")
        presenter.onBindView(itemView3, "BGN")
        verify(itemViewSelected).setMoney(any())
        verify(itemView2).setMoney(any())
        verify(itemView3).setMoney(any())
        verify(exchanger, times(2)).exchange(any(), any(), any(), any())
        reset(itemViewSelected, itemView2, itemView3, exchanger)

        repoSubject.onNext(createShortBundle())
        verify(itemViewSelected, never()).setMoney(any())
        verify(itemView2).setMoney(any())
        verify(itemView3).setMoney(any())
        verify(exchanger, times(2)).exchange(any(), any(), any(), any())
    }
}