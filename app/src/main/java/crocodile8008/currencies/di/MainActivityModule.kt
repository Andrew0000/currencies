package crocodile8008.currencies.di

import android.arch.lifecycle.ViewModelProviders
import android.view.LayoutInflater
import crocodile8008.currencies.MainActivity
import crocodile8008.currencies.presentation.viewmodel.CurrenciesViewModel
import dagger.Module
import dagger.Provides

/**
 * Created by Andrei Riik in 2018.
 */
@Module
@ActivityScope
class MainActivityModule(private val activity : MainActivity) {

    @Provides
    @ActivityScope
    fun provideActivity() : MainActivity = activity

    @Provides
    @ActivityScope
    fun provideLayoutInflater() : LayoutInflater = activity.layoutInflater

    @Provides
    fun provideActiveFilter() : CurrenciesViewModel = ViewModelProviders.of(activity).get(CurrenciesViewModel::class.java)
}
