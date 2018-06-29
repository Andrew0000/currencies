package crocodile8008.currencies.data

/**
 * Created by Andrei Riik in 2018.
 */
data class CurrenciesBundle(
        val base : String,
        val date : String = "1970-01-01",
        val rates : Map<String, Float> = HashMap()
) {
    companion object {
        val EMPTY = CurrenciesBundle("")
    }

    fun isEmpty() = this == EMPTY
}