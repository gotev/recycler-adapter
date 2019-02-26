package net.gotev.recycleradapter.ext

/**
 * Listener methods called when the user performs swipe gesture on one of the adapter items.
 * @author Aleksandar Gotev
 */
interface SwipeListener {
    /**
     * Called when the user swipes out one element
     * @param position position in the adapter
     * @param direction direction. Refer to ItemTouchHelper class constants.
     */
    fun onItemSwiped(position: Int, direction: Int)
}
