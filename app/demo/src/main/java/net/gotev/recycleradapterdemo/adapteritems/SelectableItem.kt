package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

open class SelectableItem(val label: String, val onClick: ((item: SelectableItem) -> Unit)? = null)
    : AdapterItem<SelectableItem.Holder>(label) {

    var selected = false

    override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_selectable)

    override fun onFilter(searchTerm: String) = label.contains(searchTerm)

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.toggleField.apply {
            isChecked = selected
            text = label
        }
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val toggleField: SwitchCompat = itemView.findViewById(R.id.toggle)

        init {
            toggleField.setOnClickListener {
                withAdapterItem<SelectableItem> {
                    selected = !selected
                    onClick?.invoke(this)
                }
            }
        }
    }
}
