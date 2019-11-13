package net.gotev.recycleradapterdemo.adapteritems.leavebehind

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapterdemo.R

/**
 * Base adapter item to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */
abstract class LeaveBehindAdapterItem<T : LeaveBehindViewHolder>(m: Any) : AdapterItem<T>(m) {

    override fun getLayoutId() = R.layout.item_leave_behind

}
