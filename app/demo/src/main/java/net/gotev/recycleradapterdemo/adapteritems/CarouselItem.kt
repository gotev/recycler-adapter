package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.NestedRecyclerAdapterItem
import net.gotev.recycleradapterdemo.R

/**
 * @author Aleksandar Gotev
 */
class CarouselItem(
    val title: String,
    adapter: RecyclerAdapter,
    recycledViewPool: RecyclerView.RecycledViewPool?
) : NestedRecyclerAdapterItem<CarouselItem.Holder>(adapter, recycledViewPool, title) {

    override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_titled_carousel)

    override fun bind(firstTime: Boolean, holder: Holder) {
        super.bind(firstTime, holder)
        holder.title.text = title
    }

    class Holder(itemView: View) : NestedRecyclerAdapterItem.Holder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title)
    }
}
