package net.gotev.recycleradapter;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Listener methods called when the user performs swipe gesture on one of the adapter items.
 * @author Aleksandar Gotev
 */
public interface SwipeListener {
    /**
     * Called when the user swipes out one element
     * @param position position in the adapter
     * @param direction direction. Refer to {@link ItemTouchHelper} constants.
     */
    void onItemSwiped(int position, int direction);
}
