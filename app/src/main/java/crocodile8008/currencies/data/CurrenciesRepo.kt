package crocodile8008.currencies.data

import crocodile8008.common.log.Lo
import crocodile8008.currencies.data.network.CurrenciesService
import io.reactivex.Observable
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by Andrei Riik in 2018.
 */
@Singleton
class CurrenciesRepo @Inject constructor(val service: CurrenciesService) {

    //TODO
    fun get() : Observable<CurrenciesBundle> {
        return Observable.fromCallable {
            val response: Response<Any> = service.query("EUR").execute()
            return@fromCallable parse(response)
        }
    }

    @Throws(RuntimeException::class, ClassCastException::class)
    private fun parse(response: Response<Any>) : CurrenciesBundle {
        val body = response.body()
        Lo.d("parse response: $response")
        Lo.d("parse body: $body")

        if (body == null || !(body is Map<*, *>)) {
            throw RuntimeException("unexpected body $body")
        }
        return CurrenciesBundle(
                base = body["base"] as String,
                date = body["date"] as String,
                rates = body["rates"] as Map<String, Float>
        )
    }
}