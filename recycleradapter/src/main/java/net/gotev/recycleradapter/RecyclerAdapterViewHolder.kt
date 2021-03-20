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

    fun setAdapter(recyclerAdapter: RecyclerAdapterNotifier) {
        adapter = WeakReference(recyclerAdapter)
    }

    protected fun getAdapterItem() = adapter?.get()?.getAdapterItem(this)

    /**
     * Notifies that the model associated to this ViewHolder has been changed.
     */
    protected fun notifyItemChanged() {
        adapter?.get()?.notifyItemChanged(this)
    }

    protected fun findViewById(id: Int): View = itemView.findViewById(id)

    open fun prepareForReuse() {}
}
