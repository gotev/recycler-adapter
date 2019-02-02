package net.gotev.recycleradapterdemo.adapteritems

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.ToggleButton
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_text_with_button.*

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier

import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


class TextWithButtonItem(private val text: String) : AdapterItem<TextWithButtonItem.Holder>() {

    companion object {
        private const val PARAM_PRESSED = "pressed"
    }

    private var pressed = false

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun onEvent(position: Int, data: Bundle?): Boolean {
        pressed = data?.getBoolean(PARAM_PRESSED, false) ?: false
        return true
    }

    override fun getLayoutId() = R.layout.item_text_with_button

    override fun bind(holder: Holder) {
        holder.textViewField.text = text
        holder.buttonField.isChecked = pressed
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier) : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val textViewField: TextView by lazy { textView }
        internal val buttonField: ToggleButton by lazy { toggleButton }

        init {
            buttonField.setOnClickListener {
                val data = Bundle()
                data.putBoolean(PARAM_PRESSED, buttonField.isChecked)
                sendEvent(data)
            }
        }
    }
}
