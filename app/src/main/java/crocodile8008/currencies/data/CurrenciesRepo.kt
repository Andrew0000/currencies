package crocodile8008.currencies.data

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.model.CurrenciesBundle
import crocodile8008.currencies.data.network.CurrenciesService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Andrei Riik in 2018.
 */
@Singleton
class CurrenciesRepo @Inject constructor(private val service: CurrenciesService) {

    private val updates = BehaviorSubject.createDefault<CurrenciesBundle>(CurrenciesBundle.EMPTY)
    private var updatesDisposable : Disposable? = null

    fun observeAllUpdates() : Observable<CurrenciesBundle> = updates

    fun startUpdates() {
        if (updatesDisposable != null) {
            return
        }
        updatesDisposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { service.query("EUR") }
                .doOnError { Lo.e("on error", it) }
                .retry()
                .subscribe(
                        { updates.onNext(it) },
                        { Lo.e("error", it) }
                )
    }

    fun stopUpdates() {
        updatesDisposable?.dispose()
        updatesDisposable = null
    }
}