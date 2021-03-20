package net.gotev.recycleradapterdemo.adapteritems

import android.util.TypedValue
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapter.dp

class LabelItem(private val text: String) :
    AdapterItem<LabelItem.Holder>(text) {

    /*
    <TextView xmlns:android="http://schemas.android.com/apk/res/android"
        android:gravity="center_vertical"
        android:layout_width="wrap_content"
        android:layout_margin="8dp"
        android:layout_height="48dp"
        android:textSize="18sp" />
     */

    // Variant using XML inflation
    // override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_empty)

    // Variant using code only
    override fun getView(parent: ViewGroup): View = TextView(parent.context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(parent.layoutParams).apply {
            width = WRAP_CONTENT
            height = 48.dp(context)
            gravity = CENTER_VERTICAL

            val margin = 8.dp(context)
            leftMargin = margin
            rightMargin = margin
            topMargin = margin
            bottomMargin = margin
        }

        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
    }

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.view.text = text
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val view = itemView as TextView
    }
}
