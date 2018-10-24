package crocodile8008.currencies.presentation.presenter

import com.nhaarman.mockitokotlin2.*
import crocodile8008.currencies.MockData.createShortBundle
import crocodile8008.currencies.data.CurrenciesRepo
import crocodile8008.currencies.data.model.CountryRate
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.presentation.view.CurrenciesView
import crocodile8008.currencies.presentation.view.ItemView
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Created by Andrei Riik in 2018.
 */
class CurrenciesListPresenterTest {

    @Mock
    private lateinit var repo: CurrenciesRepo
    private val viewModel  = CurrenciesViewModel()

    private lateinit var presenter: CurrenciesListPresenter

    private val repoSubject = BehaviorSubject.createDefault<CurrenciesBundle>(createShortBundle())

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        MockitoAnnotations.initMocks(this)
        presenter = CurrenciesListPresenter(repo, viewModel)
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
}