package crocodile8008.currencies.utils

import android.app.Activity
import android.view.inputmethod.InputMethodManager

/**
 * Created by Andrei Riik in 2018.
 */
object Utils {

    fun hideKeyboard(activity: Activity) {
        activity.currentFocus?.let { focusView ->
            val input = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(focusView.windowToken, 0)
        }
    }
}