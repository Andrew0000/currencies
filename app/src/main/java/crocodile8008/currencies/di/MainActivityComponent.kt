package crocodile8008.currencies.di

import crocodile8008.currencies.presentation.view.CurrenciesFragment
import dagger.Subcomponent

/**
 * Created by Andrei Riik in 2018.
 */
@MainActivityScope
@Subcomponent(modules = [(MainActivityModule::class)])
interface MainActivityComponent {

    fun inject(obj : CurrenciesFragment)
}
