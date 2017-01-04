package net.gotev.recycleradapterdemo.leavebehind;


import net.gotev.recycleradapter.AdapterItem;
import net.gotev.recycleradapterdemo.R;


/**
 * Base adapter item to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */

public abstract class LeaveBehindAdapterItem<T extends LeaveBehindViewHolder> extends AdapterItem<T> {

    @Override
    public final int getLayoutId() {
        return R.layout.item_leave_behind;
    }

}
