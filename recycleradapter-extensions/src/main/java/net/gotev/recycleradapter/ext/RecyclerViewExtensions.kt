package net.gotev.recycleradapter.ext

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.RecyclerAdapter

val sharedViewPool by lazy { RecyclerView.RecycledViewPool() }

/**
 * Applies swipe gesture detection on a RecyclerView.
 *
 * @param listener     listener called when a swipe is performed on one of the items
 */
fun RecyclerView.applySwipeGesture(listener: SwipeListener) {
    ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
            listener.onItemSwiped(viewHolder.adapterPosition, swipeDir)
        }
    }).attachToRecyclerView(this)
}

fun Context.prefetchingLinearLayoutManager(
    initialPrefetch: Int = 5,
    orientation: Int = RecyclerView.VERTICAL
) = LinearLayoutManager(this, orientation, false).apply {
    isItemPrefetchEnabled = true
    initialPrefetchItemCount = initialPrefetch
}

fun RecyclerView.withSharedViewPool() = apply {
    setRecycledViewPool(sharedViewPool)
}

fun RecyclerView.setupWithPrefetchingLinearLayoutAndSharedViewPool(
    recyclerAdapter: RecyclerAdapter,
    initialPrefetch: Int = 5
) {
    withSharedViewPool()
    layoutManager = context.prefetchingLinearLayoutManager(initialPrefetch)
    adapter = recyclerAdapter
}
