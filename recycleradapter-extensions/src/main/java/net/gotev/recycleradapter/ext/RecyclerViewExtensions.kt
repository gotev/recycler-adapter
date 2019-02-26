package net.gotev.recycleradapter.ext

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Applies swipe gesture detection on a RecyclerView.
 *
 * @param listener     listener called when a swipe is performed on one of the items
 */
fun RecyclerView.applySwipeGesture(listener: SwipeListener) {
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                            target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            listener.onItemSwiped(viewHolder.adapterPosition, swipeDir)
        }
    }).attachToRecyclerView(this)
}
