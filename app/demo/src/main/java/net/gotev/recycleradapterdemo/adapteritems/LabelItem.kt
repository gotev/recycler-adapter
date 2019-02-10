package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_empty.*

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier

import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


class LabelItem(private val text: String, private val selectionGroup: String? = null) : AdapterItem<LabelItem.Holder>() {

    override fun getSelectionGroup() = selectionGroup

    override fun getLayoutId() = R.layout.item_empty

    override fun bind(holder: Holder) {
        holder.textViewField.text = text
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier) : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val textViewField: TextView by lazy { textView }
    }
}
