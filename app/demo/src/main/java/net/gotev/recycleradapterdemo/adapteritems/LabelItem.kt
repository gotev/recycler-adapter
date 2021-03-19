package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.widget.TextView
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

class LabelItem(private val text: String) :
    AdapterItem<LabelItem.Holder>(text) {

    override fun getLayoutId() = R.layout.item_empty

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.textViewField.text = text
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val textViewField: TextView = itemView.findViewById(R.id.textView)
    }
}
