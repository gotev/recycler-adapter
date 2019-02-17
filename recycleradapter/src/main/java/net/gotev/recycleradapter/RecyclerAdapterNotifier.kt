package net.gotev.recycleradapter


/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
interface RecyclerAdapterNotifier {
    /**
     * Notifies that the cell calling this method has been selected (used in single or multiple
     * selection mode)
     */
    fun selected(holder: RecyclerAdapterViewHolder)

    /**
     * Gets the AdapterItem associated to the given ViewHolder.
     */
    fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>?

    /**
     * Notifies that the model associated to the given ViewHolder has been changed.
     */
    fun notifyItemChanged(holder: RecyclerAdapterViewHolder)
}
