package net.gotev.recycleradapterdemo.adapteritems.leavebehind

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapterdemo.R

/**
 * Base RecyclerAdapterViewHolder to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */

abstract class LeaveBehindViewHolder(itemView: View) : RecyclerAdapterViewHolder(itemView) {

    private val contentView: FrameLayout = itemView.findViewById(R.id.swipe_content_view)
    private val leaveBehindView: FrameLayout = itemView.findViewById(R.id.swipe_background_layout)

    abstract val contentViewId: Int
    abstract val leaveBehindId: Int

    init {
        LayoutInflater.from(itemView.context).apply {
            inflate(contentViewId, contentView)
            inflate(leaveBehindId, leaveBehindView)
        }
    }
}
