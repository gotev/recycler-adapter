package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

class TextWithToggleItem(private val text: String) : AdapterItem<TextWithToggleItem.Holder>(text) {

    private var pressed = false

    override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_text_with_toggle)

    override fun onFilter(searchTerm: String) = text.contains(searchTerm, ignoreCase = true)

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.textViewField.text = text
        holder.buttonField.isChecked = pressed
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val textViewField: TextView = itemView.findViewById(R.id.textView)
        internal val buttonField: SwitchCompat = itemView.findViewById(R.id.toggleButton)

        init {
            itemView.setOnClickListener {
                withAdapterItem<TextWithToggleItem> {
                    pressed = !buttonField.isChecked
                    notifyItemChanged()
                }
            }
        }
    }
}
