package net.gotev.recycleradapter

import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import android.view.View

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
    protected fun sendEvent(data: Bundle?) {
        this.adapter.get()?.sendEvent(this, data)
    }

    protected fun findViewById(id: Int): View {
        return itemView.findViewById(id)
    }
}
