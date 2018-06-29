package crocodile8008.currencies.data.network

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Andrei Riik in 2018.
 */
interface CurrenciesService {

    companion object {
        const val BASE_URL = "https://revolut.duckdns.org/"
    }

    @GET("latest")
    fun query(@Query("base") countryCode: String): Call<Any>
}
