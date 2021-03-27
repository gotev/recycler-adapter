package net.gotev.recycleradapterdemo.adapteritems.leavebehind

import android.view.View
import android.widget.TextView
import net.gotev.recycleradapterdemo.R

class MyLeaveBehindItem(private val value: String, private val background: String, private val onClick: ((MyLeaveBehindItem) -> Unit)? = null) :
    LeaveBehindAdapterItem<MyLeaveBehindItem.Holder>(value) {

    override fun onFilter(searchTerm: String) = value.contains(searchTerm, ignoreCase = true)

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.nameField.text = value
        holder.deleteField.text = background
    }

    class Holder(itemView: View) : LeaveBehindViewHolder(itemView) {

        internal val nameField: TextView = itemView.findViewById(R.id.name)
        internal val deleteField: TextView = itemView.findViewById(R.id.delete)

        override val contentViewId: Int
            get() = R.layout.swipe_foregound_layout

        override val leaveBehindId: Int
            get() = R.layout.swipe_background_layout

        init {
            leaveBehindView.onClickWith<MyLeaveBehindItem> {
                onClick?.invoke(this)
            }
        }
    }
}
