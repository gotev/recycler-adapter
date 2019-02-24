package net.gotev.recycleradapterdemo.adapteritems

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_title_subtitle.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R


open class TitleSubtitleItem(private val context: Context, private val text: String)
    : AdapterItem<TitleSubtitleItem.Holder>() {

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun getLayoutId() = R.layout.item_title_subtitle

    override fun bind(holder: Holder) {
        holder.titleField.text = text
        holder.subtitleField.text = "subtitle"
    }

    private fun onTitleClicked(position: Int) {
        showToast("clicked TITLE at position $position")
    }

    private fun onSubTitleClicked(position: Int) {
        showToast("clicked SUBTITLE at position $position")
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier)
        : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val titleField: TextView by lazy { title }
        internal val subtitleField: TextView by lazy { subtitle }

        init {
            titleField.setOnClickListener {
                (getAdapterItem() as? TitleSubtitleItem)?.apply {
                    onTitleClicked(adapterPosition)
                }
            }

            subtitleField.setOnClickListener {
                (getAdapterItem() as? TitleSubtitleItem)?.apply {
                    onSubTitleClicked(adapterPosition)
                }
            }
        }
    }
}
