package net.gotev.recycleradapter

/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
interface RecyclerAdapterNotifier {
    /**
     * Gets the AdapterItem associated to the given ViewHolder.
     */
    fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>?

    /**
     * Notifies that the model associated to the given ViewHolder has been changed.
     */
    fun notifyItemChanged(holder: RecyclerAdapterViewHolder)
}
