package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.NestedRecyclerAdapterItem
import net.gotev.recycleradapterdemo.R

/**
 * @author Aleksandar Gotev
 */
class TitledCarousel(
        val title: String,
        adapter: RecyclerAdapter,
        recycledViewPool: RecyclerView.RecycledViewPool?
) : NestedRecyclerAdapterItem<TitledCarousel.Holder>(adapter, recycledViewPool) {

    override fun getLayoutId() = R.layout.item_titled_carousel

    override fun bind(holder: Holder) {
        super.bind(holder)
        holder.title.text = title
    }

    class Holder(itemView: View) : NestedRecyclerAdapterItem.Holder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
    }
}
