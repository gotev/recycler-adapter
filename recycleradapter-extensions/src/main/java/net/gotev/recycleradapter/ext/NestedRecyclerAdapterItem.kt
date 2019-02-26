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
open class NestedRecyclerAdapterItem(val recyclerAdapter: RecyclerAdapter)
    : AdapterItem<NestedRecyclerAdapterItem.Holder>() {

    override fun getLayoutId() = R.layout.item_horizontal

    open fun getLayoutManager(context: Context): RecyclerView.LayoutManager =
            LinearLayoutManager(context, HORIZONTAL, false)

    override fun bind(holder: Holder) {
        with(holder.recyclerView) {
            // reset layout manager and adapter
            layoutManager = null
            adapter = null

            layoutManager = getLayoutManager(context)
            adapter = recyclerAdapter
        }
    }

    open class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }
}
