package net.gotev.recycleradapter

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * Base ViewHolder class to extend in subclasses.
 * @author Aleksandar Gotev
 */
abstract class RecyclerAdapterViewHolder(itemView: View, adapter: RecyclerAdapterNotifier) : RecyclerView.ViewHolder(itemView) {

    private val adapter: WeakReference<RecyclerAdapterNotifier> = WeakReference(adapter)

    /**
     * Sends an event to the adapter.
     * @param data additional event data
     */
    protected fun sendEvent(data: Bundle? = null) {
        this.adapter.get()?.sendEvent(this, data)
    }

    /**
     * Notifies the adapter that this item has been selected.
     */
    protected fun setSelected() {
        this.adapter.get()?.selected(this)
    }

    protected fun findViewById(id: Int): View {
        return itemView.findViewById(id)
    }
}
