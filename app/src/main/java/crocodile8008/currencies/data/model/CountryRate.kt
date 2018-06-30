package crocodile8008.currencies.data.model

/**
 * Created by Andrei Riik in 2018.
 */
data class CountryRate(val name : String, val rate : Float) {

    companion object {
        fun parse(name : String, rate : String) : CountryRate {
            return try {
                CountryRate(name, rate.toFloat())
            } catch (e : NumberFormatException) {
                CountryRate(name, 0f)
            }
        }
    }
}