package net.gotev.recycleradapterdemo.adapteritems

import android.util.Log
import android.view.View
import androidx.appcompat.widget.SwitchCompat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_selectable.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


open class SelectableItem(val label: String, private val group: String)
    : AdapterItem<SelectableItem.Holder>() {

    override fun getLayoutId() = R.layout.item_selectable

    override fun getSelectionGroup() = group

    override fun onFilter(searchTerm: String) = label.contains(searchTerm)

    override fun onSelectionChanged(isNowSelected: Boolean): Boolean {
        Log.i("Item", "Group: $group, Label: $label is now selected = $isNowSelected")
        return super.onSelectionChanged(isNowSelected)
    }

    override fun bind(holder: Holder) {
        holder.toggleField.apply {
            isChecked = selected
            text = label
        }
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val toggleField: SwitchCompat by lazy { toggle }

        init {
            toggleField.setOnClickListener {
                setSelected()
            }
        }
    }
}
