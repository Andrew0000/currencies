package crocodile8008.currencies.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Andrei Riik in 2018.
 *
 * See Test
 */

fun <T> Observable<T>.subscribeDisposable(onNext: (T) -> Unit, disposable: CompositeDisposable) {
    disposable.add(
            subscribe(onNext, { } )
    )
}

fun EditText.showKeyboard() {
    requestFocus()
    val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    keyboard.showSoftInput(this, 0)
}

fun Float.format2Digits() = "%.2f".format(this)

fun String.toFloatOrZero() : Float {
    return try {
        this.replace(",", ".").toFloat()
    } catch (e : NumberFormatException) {
        0f
    }
}