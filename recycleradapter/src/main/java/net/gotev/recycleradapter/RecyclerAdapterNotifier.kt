package net.gotev.recycleradapter

import android.os.Bundle


/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
interface RecyclerAdapterNotifier {
    /**
     * Sends an event from the holder to the item.
     */
    fun sendEvent(holder: RecyclerAdapterViewHolder, data: Bundle?)

    /**
     * Notifies that the cell calling this method has been selected (used in single or multiple
     * selection mode)
     */
    fun selected(holder: RecyclerAdapterViewHolder)
}
