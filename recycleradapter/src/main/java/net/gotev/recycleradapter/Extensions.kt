package net.gotev.recycleradapter

import androidx.paging.DataSource

/**
 * @author Aleksandar Gotev
 */
internal fun AdapterItem<*>?.viewType() = this?.javaClass?.name?.hashCode() ?: 0
internal fun Class<out AdapterItem<*>>.viewType() = hashCode()

@Suppress("UNCHECKED_CAST")
internal fun <T : RecyclerAdapterViewHolder> AdapterItem<out T>.castAsIn(): AdapterItem<in T> {
    return this as AdapterItem<in T>
}

@Suppress("UNCHECKED_CAST")
internal fun <T> DataSource<*, *>.casted() = this as DataSource<T, AdapterItem<*>>