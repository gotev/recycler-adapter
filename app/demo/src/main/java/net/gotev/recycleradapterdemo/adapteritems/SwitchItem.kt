package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapter.dp

open class SwitchItem(
    val label: String,
    val onClick: ((item: SwitchItem) -> Unit)? = null
) : AdapterItem<SwitchItem.Holder>(label) {

    var selected = false

    override fun getView(parent: ViewGroup): View = SwitchCompat(parent.context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(parent.layoutParams).apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = 36.dp(context)

            val margin = 8.dp(context)
            leftMargin = margin
            rightMargin = margin
            topMargin = margin
            bottomMargin = margin
        }
    }

    override fun onFilter(searchTerm: String) = label.contains(searchTerm)

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.view.apply {
            isChecked = selected
            text = label
        }
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val view = itemView as SwitchCompat

        init {
            view.onClickWith<SwitchItem> {
                selected = !selected
                onClick?.invoke(this)
            }
        }
    }
}
