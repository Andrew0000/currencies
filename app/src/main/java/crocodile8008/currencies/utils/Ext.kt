package crocodile8008.currencies.utils

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Andrei Riik in 2018.
 */

fun <T> Observable<T>.subscribeAndAddToDisposable(onNext: (T) -> Unit,
                                                  onError: (Throwable) -> Unit,
                                                  disposable: CompositeDisposable) {
    disposable.add(
            subscribe(onNext, onError)
    )
}

fun EditText.showKeyboard() {
    requestFocus()
    val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    keyboard.showSoftInput(this, 0)
}