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

    @Deprecated(
        message = "use withAdapterItem<Type> { } instead",
        level = DeprecationLevel.WARNING
    )
    protected fun getAdapterItem() = adapter?.get()?.getAdapterItem(this)

    @Suppress("UNCHECKED_CAST")
    protected fun <T: AdapterItem<*>> withAdapterItem(action: T.() -> Unit) {
        (adapter?.get()?.getAdapterItem(this) as? T)?.apply(action)
    }

    /**
     * Notifies that the model associated to this ViewHolder has been changed.
     */
    protected fun notifyItemChanged() {
        adapter?.get()?.notifyItemChanged(this)
    }

    protected fun findViewById(id: Int): View = itemView.findViewById(id)

    open fun prepareForReuse() {}
}
