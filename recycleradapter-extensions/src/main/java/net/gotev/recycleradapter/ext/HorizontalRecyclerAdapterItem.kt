package net.gotev.recycleradapter.ext

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.HORIZONTAL
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder

/**
 * @author Aleksandar Gotev
 */
class HorizontalRecyclerAdapterItem(val recyclerAdapter: RecyclerAdapter)
    : AdapterItem<HorizontalRecyclerAdapterItem.Holder>() {

    override fun getLayoutId() = R.layout.item_horizontal

    override fun bind(holder: Holder) {
        with(holder.recyclerView) {
            // reset layout manager and adapter
            layoutManager = null
            adapter = null

            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            adapter = recyclerAdapter
        }
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier)
        : RecyclerAdapterViewHolder(itemView, adapter) {

        val recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }
}
