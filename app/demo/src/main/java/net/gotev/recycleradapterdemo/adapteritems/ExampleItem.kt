package net.gotev.recycleradapterdemo.adapteritems

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_example.*

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


open class ExampleItem(private val context: Context, private val text: String)
    : AdapterItem<ExampleItem.Holder>() {

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun getLayoutId() = R.layout.item_example

    override fun onEvent(position: Int, data: Bundle?): Boolean {
        if (data == null)
            return false

        val clickEvent = data.getString("click") ?: return false

        if ("title" == clickEvent) {
            Toast.makeText(context, "clicked TITLE at position $position", Toast.LENGTH_SHORT).show()
        } else if ("subtitle" == clickEvent) {
            Toast.makeText(context, "clicked SUBTITLE at position $position", Toast.LENGTH_SHORT).show()
        }

        return false
    }

    override fun bind(holder: Holder) {
        holder.titleField.text = text
        holder.subtitleField.text = "subtitle"
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier)
        : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val titleField: TextView by lazy { title }
        internal val subtitleField: TextView by lazy { subtitle }

        init {
            titleField.setOnClickListener {
                val data = Bundle()
                data.putString("click", "title")
                sendEvent(data)
            }

            subtitleField.setOnClickListener {
                val data = Bundle()
                data.putString("click", "subtitle")
                sendEvent(data)
            }
        }
    }
}
