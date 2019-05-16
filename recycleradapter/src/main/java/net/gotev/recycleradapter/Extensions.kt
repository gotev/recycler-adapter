package net.gotev.recycleradapter

/**
 * @author Aleksandar Gotev
 */
fun AdapterItem<*>?.viewType() = this?.javaClass?.name?.hashCode() ?: 0
fun Class<out AdapterItem<*>>.viewType() = hashCode()

@Suppress("UNCHECKED_CAST")
fun <T : RecyclerAdapterViewHolder> AdapterItem<out T>.castAsIn(): AdapterItem<in T> {
    return this as AdapterItem<in T>
}
