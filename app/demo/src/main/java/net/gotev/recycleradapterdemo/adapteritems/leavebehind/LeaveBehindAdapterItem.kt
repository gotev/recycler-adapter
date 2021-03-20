package net.gotev.recycleradapterdemo.adapteritems.leavebehind

import android.view.View
import android.view.ViewGroup
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapterdemo.R

/**
 * Base adapter item to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */
abstract class LeaveBehindAdapterItem<T : LeaveBehindViewHolder>(model: Any) : AdapterItem<T>(model) {

    override fun getView(parent: ViewGroup): View = parent.inflating(R.layout.item_leave_behind)

}
