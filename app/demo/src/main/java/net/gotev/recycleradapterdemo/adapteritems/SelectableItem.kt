package net.gotev.recycleradapterdemo.adapteritems

import android.util.Log
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


open class SelectableItem(val label: String, private val group: String)
    : AdapterItem<SelectableItem.Holder>(label) {

    override fun getLayoutId() = R.layout.item_selectable

    override fun getSelectionGroup() = group

    override fun onFilter(searchTerm: String) = label.contains(searchTerm)

    override fun onSelectionChanged(isNowSelected: Boolean): Boolean {
        Log.i("Item", "Group: $group, Label: $label is now selected = $isNowSelected")
        return super.onSelectionChanged(isNowSelected)
    }

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
                setSelected()
            }
        }
    }
}
