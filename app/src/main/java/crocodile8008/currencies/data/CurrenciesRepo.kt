package crocodile8008.currencies.data

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.network.CurrenciesService
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import retrofit2.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Andrei Riik in 2018.
 */
@Singleton
class CurrenciesRepo @Inject constructor(private val service: CurrenciesService) {

    //TODO connectable better?
    private val updates = BehaviorSubject.createDefault<CurrenciesBundle>(CurrenciesBundle.EMPTY)
    private var updatesDisposable : Disposable? = null
    private var last = CurrenciesBundle.EMPTY

    fun observeAllUpdates() : Observable<CurrenciesBundle> = updates;

    fun startUpdates() {
        if (updatesDisposable != null) {
            return
        }
        updatesDisposable = Observable
                .interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .switchMap { loadCurrencies() }
                .retry()
                .subscribe(
                        {
                            last = it
                            updates.onNext(it)
                        },
                        { Lo.e("error", it) }
                )
    }

    fun stopUpdates() {
        updatesDisposable?.dispose()
        updatesDisposable = null
    }

    private fun loadCurrencies() : Observable<CurrenciesBundle> {
        return Observable.fromCallable {
            val response: Response<Any> = service.query("EUR").execute()
            return@fromCallable parse(response)
        }
    }

    @Throws(RuntimeException::class, ClassCastException::class)
    private fun parse(response: Response<Any>) : CurrenciesBundle {
        val body = response.body()
        Lo.v("parse: $body, $response")

        if (body == null || !(body is Map<*, *>)) {
            throw RuntimeException("unexpected body $body")
        }
        return CurrenciesBundle(
                base = body["base"] as String,
                date = body["date"] as String,
                rates = (body["rates"] as Map<String, Double>).mapValues { it.value.toFloat() }
        )
    }
}