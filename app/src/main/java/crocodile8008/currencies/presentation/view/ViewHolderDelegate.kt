package crocodile8008.currencies.presentation.view

/**
 * Created by Andrei Riik in 2018.
 */
interface ViewHolderDelegate<T1, T2> {

    fun onBindViewHolder(first: T1, second: T2)
}