package net.gotev.recycleradapter

import android.content.Context

/**
 * @author Aleksandar Gotev
 */
fun AdapterItem<*>?.viewType() = this?.javaClass?.name?.hashCode() ?: 0

@Suppress("UNCHECKED_CAST")
fun <T : RecyclerAdapterViewHolder> AdapterItem<out T>.castAsIn(): AdapterItem<in T> {
    return this as AdapterItem<in T>
}

fun Int.dp(context: Context): Int = (this * context.resources.displayMetrics.density + 0.5f).toInt()
