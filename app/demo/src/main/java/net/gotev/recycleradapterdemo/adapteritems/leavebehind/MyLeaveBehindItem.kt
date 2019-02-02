package net.gotev.recycleradapterdemo.adapteritems.leavebehind

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.swipe_background_layout.*
import kotlinx.android.synthetic.main.swipe_foregound_layout.*
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapterdemo.R

class MyLeaveBehindItem(private val value: String, private val background: String) : LeaveBehindAdapterItem<MyLeaveBehindItem.Holder>() {

    override fun bind(holder: Holder) {
        holder.nameField.text = value
        holder.deleteField.text = background
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier) : LeaveBehindViewHolder(itemView, adapter) {

        internal val nameField: TextView by lazy { name }
        internal val deleteField: TextView by lazy { delete }

        override val contentViewId: Int
            get() = R.layout.swipe_foregound_layout

        override val leaveBehindId: Int
            get() = R.layout.swipe_background_layout
    }
}
