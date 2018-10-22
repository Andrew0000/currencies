package crocodile8008.currencies.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Created by Andrei Riik in 2018.
 */
object Utils {

    fun hideKeyboard(activity: Activity) {
        if (activity.currentFocus != null) {
            val input = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)
        }
    }
}