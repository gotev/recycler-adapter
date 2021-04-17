package net.gotev.recycleradapter.ext

import android.content.Context
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import java.util.Collections

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
            listener.onItemSwiped(viewHolder.bindingAdapterPosition, swipeDir)
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

fun RecyclerView.withSharedViewPool(pool: RecyclerView.RecycledViewPool = sharedViewPool) = apply {
    setRecycledViewPool(pool)
}

fun RecyclerView.setupWithPrefetchingLinearLayoutAndSharedViewPool(
    recyclerAdapter: RecyclerAdapter,
    initialPrefetch: Int = 5,
    pool: RecyclerView.RecycledViewPool = sharedViewPool
) {
    withSharedViewPool(pool)
    layoutManager = context.prefetchingLinearLayoutManager(initialPrefetch)
    adapter = recyclerAdapter
}

/**
 * Prevent RecyclerView from scrolling when adding many items
 * Taken from: https://github.com/airbnb/epoxy/issues/224#issuecomment-305991898
 *
 * @param layoutManager RecyclerView's Layout Manager
 */
fun RecyclerAdapter.lockScrollingWhileInserting(layoutManager: RecyclerView.LayoutManager) {
    registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            if (positionStart == 0) {
                layoutManager.scrollToPosition(0)
            }
        }
    })
}

/**
 * Enables reordering of the list through drag and drop, which is activated when the user
 * long presses on an item.
 *
 * @param recyclerView recycler view on which to apply the drag and drop
 * @param directions directions on which to enable drag and drop gestures. By default it's
 *                   DOWN or UP but you can set it to DOWN or UP or START or END in case you
 *                   have a grid layout and you want also to drag and drop in all directions
 */
fun RecyclerAdapter.enableDragDrop(recyclerView: RecyclerView, directions: Int = ItemTouchHelper.DOWN or ItemTouchHelper.UP) {
    val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                directions
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val sourcePosition = viewHolder.bindingAdapterPosition
            val targetPosition = target.bindingAdapterPosition

            swap(sourcePosition, targetPosition)

            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            //Do nothing here
        }
    })

    touchHelper.attachToRecyclerView(recyclerView)
}

/**
 * Gets a copy of the internal list on which you can do whatever you like. Returned list will
 * be synced and rendered.
 *
 * Example 1
 *
 *  // remove last item with class TextWithToggleItem
 *  recyclerAdapter.modifyItemsAndRender { items ->
 *    items.apply {
 *      remove(lastOrNull { it::class.java == TextWithToggleItem::class.java })
 *    }
 *  }
 *
 * Example 2
 *
 *  // sort items
 *  recyclerAdapter.modifyItemsAndRender { it.sorted() }
 *
 * @param action lambda which has a copy of the current list and which returns the new list to be rendered
 */
fun RecyclerAdapter.modifyItemsAndRender(action: (items: ArrayList<out AdapterItem<*>>) -> List<AdapterItem<*>>) {
    syncWithItems(ArrayList(action(adapterItems)))
}
