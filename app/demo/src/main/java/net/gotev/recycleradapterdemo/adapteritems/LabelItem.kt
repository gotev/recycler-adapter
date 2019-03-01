package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.widget.TextView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_empty.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


class LabelItem(private val text: String, private val selectionGroup: String? = null) : AdapterItem<LabelItem.Holder>() {

    override fun getSelectionGroup() = selectionGroup

    override fun getLayoutId() = R.layout.item_empty

    override fun bind(holder: Holder) {
        holder.textViewField.text = text
    }

    override fun hasToBeReplacedBy(newItem: AdapterItem<*>): Boolean {
        if (newItem !is LabelItem)
            return true

        return false
    }

    override fun equals(other: Any?): Boolean {
        return other is LabelItem && text == other.text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val textViewField: TextView by lazy { textView }
    }
}
