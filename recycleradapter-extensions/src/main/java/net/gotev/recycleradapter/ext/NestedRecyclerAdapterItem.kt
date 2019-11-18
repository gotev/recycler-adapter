package net.gotev.recycleradapter.ext

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.RecyclerAdapterViewHolder

/**
 * @author Aleksandar Gotev
 */
abstract class NestedRecyclerAdapterItem<T : NestedRecyclerAdapterItem.Holder>(
        val recyclerAdapter: RecyclerAdapter,
        val recycledViewsPool: RecyclerView.RecycledViewPool?,
        model: Any
) : AdapterItem<T>(model) {

    override fun getLayoutId() = R.layout.item_nested

    open fun getLayoutManager(context: Context): RecyclerView.LayoutManager =
            LinearLayoutManager(context, HORIZONTAL, false)

    override fun bind(firstTime: Boolean, holder: T) {
        with(holder.recyclerView) {
            // reset layout manager and adapter
            layoutManager = null
            adapter = null

            layoutManager = getLayoutManager(context)
            adapter = recyclerAdapter
            recycledViewsPool?.let {
                setRecycledViewPool(it)
            }
        }
    }

    open class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }
}
