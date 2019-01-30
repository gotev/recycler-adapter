package net.gotev.recycleradapterdemo.leavebehind

import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_leave_behind.*
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder

/**
 * Base RecyclerAdapterViewHolder to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */

abstract class LeaveBehindViewHolder(itemView: View, adapter: RecyclerAdapterNotifier)
    : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

    override val containerView: View?
        get() = itemView

    private val contentView: FrameLayout by lazy { swipe_content_view }
    private val leaveBehindView: FrameLayout by lazy { swipe_background_layout }

    abstract val contentViewId: Int
    abstract val leaveBehindId: Int

    init {
        LayoutInflater.from(itemView.context).apply {
            inflate(contentViewId, contentView)
            inflate(leaveBehindId, leaveBehindView)
        }
    }

}
