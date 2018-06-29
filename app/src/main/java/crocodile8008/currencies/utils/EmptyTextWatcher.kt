package crocodile8008.currencies.utils

import android.text.Editable
import android.text.TextWatcher

/**
 * Created by Andrei Riik in 2018.
 */
open class EmptyTextWatcher : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {

    }
}