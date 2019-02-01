package net.gotev.recycleradapter

import android.os.Bundle


/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
interface RecyclerAdapterNotifier {
    fun sendEvent(holder: RecyclerAdapterViewHolder, data: Bundle?)
}
