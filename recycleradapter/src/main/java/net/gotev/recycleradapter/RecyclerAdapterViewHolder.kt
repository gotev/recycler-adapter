package net.gotev.recycleradapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * Base ViewHolder class to extend in subclasses.
 * @author Aleksandar Gotev
 */
abstract class RecyclerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var adapter: WeakReference<RecyclerAdapterNotifier>? = null

    internal fun setAdapter(recyclerAdapter: RecyclerAdapterNotifier) {
        adapter = WeakReference(recyclerAdapter)
    }

    protected fun getAdapterItem() = adapter?.get()?.getAdapterItem(this)

    /**
     * Notifies the adapter that this item has been selected.
     */
    protected fun setSelected() {
        adapter?.get()?.selected(this)
    }

    /**
     * Notifies that the model associated to this ViewHolder has been changed.
     */
    protected fun notifyItemChanged() {
        adapter?.get()?.notifyItemChanged(this)
    }

    protected fun findViewById(id: Int): View {
        return itemView.findViewById(id)
    }
}
