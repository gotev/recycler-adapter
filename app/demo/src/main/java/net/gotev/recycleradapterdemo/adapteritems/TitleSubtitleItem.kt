package net.gotev.recycleradapterdemo.adapteritems

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

open class TitleSubtitleItem(private val title: String, private val subtitle: String) :
    AdapterItem<TitleSubtitleItem.Holder>(title) {

    override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_title_subtitle)

    override fun onFilter(searchTerm: String) = title.contains(searchTerm, ignoreCase = true)

    override fun bind(firstTime: Boolean, holder: Holder) {
        holder.titleField.text = title
        holder.subtitleField.text = subtitle
    }

    class Holder(itemView: View) : RecyclerAdapterViewHolder(itemView) {
        internal val titleField: TextView = itemView.findViewById(R.id.title)
        internal val subtitleField: TextView = itemView.findViewById(R.id.subtitle)

        init {
            titleField.onClickWith<TitleSubtitleItem> {
                Toast.makeText(
                    itemView.context,
                    "clicked TITLE at position $bindingAdapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }

            subtitleField.onClickWith<TitleSubtitleItem> {
                Toast.makeText(
                    itemView.context,
                    "clicked SUBTITLE at position $bindingAdapterPosition",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
