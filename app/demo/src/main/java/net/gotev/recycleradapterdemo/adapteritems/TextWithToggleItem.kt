package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text_with_toggle.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


class TextWithToggleItem(private val text: String) : AdapterItem<TextWithToggleItem.Holder>() {

    private var pressed = false

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun getLayoutId() = R.layout.item_text_with_toggle

    override fun bind(holder: Holder) {
        holder.textViewField.text = text
        holder.buttonField.isChecked = pressed
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier) : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val textViewField: TextView by lazy { textView }
        internal val buttonField: SwitchCompat by lazy { toggleButton }

        init {
            containerView?.setOnClickListener {
                (getAdapterItem() as? TextWithToggleItem)?.apply {
                    pressed = !buttonField.isChecked
                    notifyItemChanged()
                }
            }
        }
    }
}
