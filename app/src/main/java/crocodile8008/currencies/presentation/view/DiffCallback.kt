package crocodile8008.currencies.presentation.view

import android.support.v7.util.DiffUtil

/**
 * Created by Andrei Riik in 2018.
 */
class DiffCallback constructor(
        private val old: List<Pair<String, Float>>,
        private val current: List<Pair<String, Float>>) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return old[oldItemPosition].first == current[newItemPosition].first
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // return false to force call onBindViewHolder() again with actual position
        return false
    }

    override fun getOldListSize() = old.size

    override fun getNewListSize() = current.size
}