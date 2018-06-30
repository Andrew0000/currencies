package crocodile8008.currencies.di

import android.content.Context
import crocodile8008.currencies.App
import crocodile8008.currencies.data.network.CurrenciesService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * Created by Andrei Riik in 2018.
 */
@Module
class AppModule(private val app : App) {

    @Provides
    @Singleton
    fun provideContext() : Context {
        return app
    }

    @Provides
    @Singleton
    fun provideCurrenciesService() : CurrenciesService {
        val retrofit = Retrofit.Builder()
                .baseUrl(CurrenciesService.BASE_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        return retrofit.create(CurrenciesService::class.java)
    }
}
