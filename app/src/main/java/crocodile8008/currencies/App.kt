package crocodile8008.currencies

import android.app.Application
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import crocodile8008.common.log.Lo
import crocodile8008.currencies.di.*

/**
 * Created by Andrei Riik in 2018.
 */
class App : Application() {

    companion object {
        @JvmStatic lateinit var instance: App
    }

    lateinit var refWatcher : RefWatcher

    private lateinit var appComponent : AppComponent
    private var mainActivityComponent : MainActivityComponent? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return
        }
        Lo.init(BuildConfig.DEBUG, "currencies_")
        refWatcher = LeakCanary.install(this)
        appComponent = DaggerAppComponent.builder().appModule(AppModule(this)).build()
    }

    fun getAppComponent() : AppComponent {
        return appComponent
    }

    @Throws(NullPointerException::class)
    fun getMainActivityComponent() : MainActivityComponent {
        val component = mainActivityComponent
        if (component == null) {
            throw NullPointerException()
        }
        return component
    }

    fun createActivityComponent(activity : MainActivity) {
        mainActivityComponent = appComponent.createMainActivityComponent(MainActivityModule(activity))
    }

    fun destroyMainActivityComponent() {
        mainActivityComponent = null
    }
}