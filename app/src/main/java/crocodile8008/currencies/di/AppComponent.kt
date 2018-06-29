package crocodile8008.currencies.di

import dagger.Component
import javax.inject.Singleton

@Component(modules = [(AppModule::class)])
@Singleton
interface AppComponent {

    fun createMainActivityComponent(module : MainActivityModule) : MainActivityComponent

    @Component.Builder
    interface Builder {
        fun appModule(app: AppModule): Builder
        fun build(): AppComponent
    }
}
