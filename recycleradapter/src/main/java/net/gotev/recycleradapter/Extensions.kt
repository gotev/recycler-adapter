package net.gotev.recycleradapter

/**
 * @author Aleksandar Gotev
 */
fun AdapterItem<*, *>?.viewType() = this?.javaClass?.name?.hashCode() ?: 0
fun Class<out AdapterItem<*, *>>.viewType() = hashCode()

@Suppress("UNCHECKED_CAST")
fun <T : RecyclerAdapterViewHolder, Model> AdapterItem<out T, out Model>.castAsIn(): AdapterItem<in T, in Model> {
    return this as AdapterItem<in T, in Model>
}
