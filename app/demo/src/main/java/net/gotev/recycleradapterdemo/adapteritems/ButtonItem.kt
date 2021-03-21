package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import com.google.android.material.button.MaterialButton
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapter.dp

class ButtonItem(private val text: String, private val onClick: (() -> Unit)? = null) :
    AdapterItem<ButtonItem.Holder>(text) {

    override fun getView(parent: ViewGroup): View = MaterialButton(parent.context).apply {
        layoutParams = ViewGroup.MarginLayoutParams(parent.layoutParams).apply {
            width = MATCH_PARENT
            height = WRAP_CONTENT

            val margin = 8.dp(context)
            leftMargin = margin
            rightMargin = margin
            topMargin = margin
            bottomMargin = margin
        }
    }

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.view.text = text
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val view = itemView as MaterialButton

        init {
            view.onClickWith<ButtonItem> {
                onClick?.invoke()
            }
        }
    }
}
