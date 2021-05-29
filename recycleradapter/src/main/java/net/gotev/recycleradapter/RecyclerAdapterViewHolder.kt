package net.gotev.recycleradapter

import android.os.SystemClock
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference

/**
 * Base ViewHolder class to extend in subclasses.
 * @author Aleksandar Gotev
 */
abstract class RecyclerAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    protected var adapter: WeakReference<RecyclerAdapterNotifier>? = null

    fun setAdapter(recyclerAdapter: RecyclerAdapterNotifier) {
        adapter = WeakReference(recyclerAdapter)
    }

    /**
     * Performs an action by safely getting the associated AdapterItem.
     */
    @Suppress("UNCHECKED_CAST")
    protected inline fun <T: AdapterItem<*>> withAdapterItem(action: T.() -> Unit) {
        (adapter?.get()?.getAdapterItem(this) as? T)?.apply(action)
    }

    /**
     * Performs an onClick action by safely getting the associated AdapterItem.
     *
     * Implements the "throttle first" mechanism for click listeners, to prevent double taps.
     *
     * How it works:
     * - Define a sampling window time (default: 500ms)
     * - when you click at time T0, the first click gets dispatched and the subsequent ones happening
     *   between T0 and T0 + WindowTime gets ignored
     */
    protected inline fun <T: AdapterItem<*>> View.onClickWith(throttleTime: Int = 500, crossinline action: T.() -> Unit) {
        onClick(throttleTime) { withAdapterItem(action) }
    }

    /**
     * Got from: https://gist.github.com/gotev/0db92be46d68aad34ee262b271b5b1bd
     *
     * Implements the "throttle first" mechanism for click listeners, to prevent double taps.
     *
     * How it works:
     * - Define a sampling window time (default: 500ms)
     * - when you click at time T0, the first click gets dispatched and the subsequent ones happening
     *   between T0 and T0 + WindowTime gets ignored
     */
    protected inline fun View.onClick(throttleTime: Int = 500, crossinline listener: (View) -> Unit) {
        var clickTime = 0L

        setOnClickListener {
            if (SystemClock.uptimeMillis() <= (clickTime + throttleTime)) return@setOnClickListener
            clickTime = SystemClock.uptimeMillis()
            listener(it)
        }
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
