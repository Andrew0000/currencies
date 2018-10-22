package crocodile8008.currencies.di

import crocodile8008.currencies.presentation.view.CurrenciesFragment
import dagger.Subcomponent

/**
 * Created by Andrei Riik in 2018.
 */
@ActivityScope
@Subcomponent(modules = [(MainActivityModule::class)])
interface MainActivityComponent {

    fun inject(obj : CurrenciesFragment)
}
