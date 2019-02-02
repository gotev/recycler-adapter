package net.gotev.recycleradapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.Pair
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.LinkedHashMap

/**
 * Makes the use of RecyclerView easier, modular and less error-prone
 *
 * @author Aleksandar Gotev
 */
class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapterViewHolder>(), RecyclerAdapterNotifier {

    companion object {
        /**
         * Applies swipe gesture detection on a RecyclerView items.
         *
         * @param recyclerView recycler view o which to apply the swipe gesture
         * @param listener     listener called when a swipe is performed on one of the items
         */
        fun applySwipeGesture(recyclerView: RecyclerView, listener: SwipeListener) {
            ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
                    0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                    listener.onItemSwiped(viewHolder.adapterPosition, swipeDir)
                }
            }).attachToRecyclerView(recyclerView)
        }
    }

    private val itemsList = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()

    private val typeIds = LinkedHashMap<String, Int>()
    private val types = LinkedHashMap<Int, AdapterItem<*>>()

    private var emptyItem: AdapterItem<in RecyclerAdapterViewHolder>? = null
    private var emptyItemId = 0

    private var filtered = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()
    private var showFiltered = false

    private val selectionGroups = LinkedHashMap<String, Boolean>()

    private val items
        get() = if (showFiltered) filtered else itemsList

    @Suppress("UNCHECKED_CAST")
    private fun <T : RecyclerAdapterViewHolder> AdapterItem<out T>.castAsIn(): AdapterItem<in T> {
        return this as AdapterItem<in T>
    }

    private fun Int.isOutOfItemsRange() = this < 0 || this >= items.size

    private fun AdapterItem<*>.changeSelectionStatus(newStatus: Boolean, position: Int) {
        this.let {
            selected = newStatus
            if (onSelectionChanged(isNowSelected = newStatus)) {
                notifyItemChanged(position)
            }
        }
    }

    private fun adapterIsEmptyAndEmptyItemIsDefined() = items.isEmpty() && emptyItem != null

    private fun updateItemAtPosition(item: AdapterItem<in RecyclerAdapterViewHolder>,
                                     position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

    private fun registerItemType(item: AdapterItem<*>) {
        val className = item.javaClass.name

        if (!typeIds.containsKey(className)) {
            val viewId = View.generateViewId()
            typeIds[className] = viewId
            types[viewId] = item
        }
    }

    private fun removeEmptyItemIfItHasBeenConfigured() {
        // this is necessary to prevent IndexOutOfBoundsException on RecyclerView when the
        // first item gets added and an empty item has been configured
        if (items.size == 1 && emptyItem != null) {
            notifyItemRemoved(0)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (adapterIsEmptyAndEmptyItemIsDefined()) {
            return emptyItemId
        }

        return typeIds.getValue(items[position].javaClass.name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterViewHolder {
        val item = if (adapterIsEmptyAndEmptyItemIsDefined() && viewType == emptyItemId) {
            emptyItem!!
        } else {
            types.getValue(viewType)
        }

        try {
            val inflater = LayoutInflater.from(parent.context)
            val view = inflater.inflate(item.getLayoutId(), parent, false)
            return item.getViewHolder(view, this)

        } catch (exc: Throwable) {
            val message = when (exc) {
                is NoSuchMethodException -> {
                    "You should declare a constructor like this in your ViewHolder:\n" +
                            "public RecyclerAdapterViewHolder(View itemView, RecyclerAdapterNotifier adapter)"
                }

                is IllegalAccessException -> {
                    "Your ViewHolder class in ${item.javaClass.name} should be public!"
                }

                else -> {
                    ""
                }
            }

            throw RuntimeException("${javaClass.simpleName} - onCreateViewHolder error. $message", exc)
        }

    }

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        if (adapterIsEmptyAndEmptyItemIsDefined()) {
            emptyItem?.bind(holder)
        } else {
            items[position].bind(holder)
        }
    }

    override fun getItemCount() = if (adapterIsEmptyAndEmptyItemIsDefined()) 1 else items.size

    override fun sendEvent(holder: RecyclerAdapterViewHolder, data: Bundle?) {
        val position = holder.adapterPosition.takeIf { !it.isOutOfItemsRange() } ?: return

        if (items[position].onEvent(position, data)) {
            notifyItemChanged(position)
        }
    }

    override fun selected(holder: RecyclerAdapterViewHolder) {
        val position = holder.adapterPosition.takeIf { !it.isOutOfItemsRange() } ?: return

        items.let { items ->
            val selectionGroup = items[position].getSelectionGroup() ?: return@let
            val multiSelect = canSelectMultipleItems(selectionGroup)
            val currentItem = items[position]

            val newStatus = !(multiSelect && currentItem.selected)
            currentItem.changeSelectionStatus(newStatus = newStatus, position = position)

            if (!multiSelect) {
                items.forEachIndexed { index, item ->
                    if (item.getSelectionGroup().equals(selectionGroup) && index != position) {
                        item.changeSelectionStatus(newStatus = false, position = index)
                    }
                }
            }
        }
    }

    /**
     * Sets the item to show when the recycler adapter is empty.
     *
     * @param item item to show when the recycler adapter is empty
     */
    fun setEmptyItem(item: AdapterItem<*>) {
        emptyItem = item.castAsIn()
        emptyItemId = View.generateViewId()

        if (items.isEmpty())
            notifyItemInserted(0)
    }

    /**
     * Adds a new item to this adapter
     *
     * @param item item to add
     * @param position position at which to add the element. If null, the element will be added
     * at the end of the list, otherwise the item will be inserted at (position) and all the
     * existing items starting from (position) will be shifted forward.
     * @return [RecyclerAdapter]
     */
    fun add(item: AdapterItem<*>, position: Int? = null): RecyclerAdapter {
        val insertPosition = if (position != null) {
            items.add(position, item.castAsIn())
            position
        } else {
            items.add(item.castAsIn())
            items.lastIndex
        }

        registerItemType(item)
        removeEmptyItemIfItHasBeenConfigured()
        notifyItemInserted(insertPosition)
        return this
    }

    /**
     * Adds an item into the adapter or updates it if already existing.
     *
     * For the update to work properly, all the items has to override the [AdapterItem.equals]
     * and [AdapterItem.hashCode] methods and implement the required business logic code to detect
     * if two instances are referring to the same item.
     *
     * If the item already exists in the list, by implementing [AdapterItem.hasToBeReplacedBy]
     * in your AdapterItem, you can decide when the new item should replace the existing one in
     * the list, reducing the workload of the recycler view.
     *
     * Check [AdapterItem.hasToBeReplacedBy] method JavaDoc for more information.
     *
     * @param item item to add or update
     * @return [RecyclerAdapter]
     */
    fun addOrUpdate(item: AdapterItem<in RecyclerAdapterViewHolder>): RecyclerAdapter {
        val itemIndex = items.indexOf(item).takeIf { it >= 0 } ?: return add(item)

        if (items[itemIndex].hasToBeReplacedBy(item)) {
            updateItemAtPosition(item, itemIndex)
        }

        return this
    }

    /**
     * Gets the position of an item in an adapter.
     *
     * For this method to work properly, all the items has to override the [AdapterItem.equals]
     * and [AdapterItem.hashCode] methods and implement the required business logic code to detect
     * if two instances are referring to the same item.
     *
     * @param item item object
     * @return the item's position or -1 if the item does not exist
     */
    fun getItemPosition(item: AdapterItem<*>) = items.indexOf(item)

    /**
     * Syncs the internal list of items with a list passed as parameter.
     * Adds, updates or deletes internal items, with RecyclerView animations.
     *
     *
     * For the sync to work properly, all the items has to override the
     * [AdapterItem.equals] and [AdapterItem.hashCode] methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item. Check the example in [RecyclerAdapter.add].
     * If two instances are referring to the same item, you can decide if the item should be
     * replaced by the new one, by implementing [AdapterItem.hasToBeReplacedBy].
     * Check hasToBeReplacedBy method JavaDoc for more information.
     *
     * @param newItems list of new items. Passing a null or empty list will result in
     * [RecyclerAdapter.clear] method call.
     * @return [RecyclerAdapter]
     */
    fun syncWithItems(newItems: ArrayList<out AdapterItem<*>>): RecyclerAdapter {
        if (newItems.isEmpty()) {
            clear()
            return this
        }

        val iterator = items.listIterator()

        while (iterator.hasNext()) {
            val internalListIndex = iterator.nextIndex()
            val item = iterator.next()
            val indexInNewItemsList = newItems.indexOf(item)

            // if the item does not exist in the new list, it means it has been deleted
            if (indexInNewItemsList < 0) {
                iterator.remove()
                notifyItemRemoved(internalListIndex)
            } else { // the item exists in the new list
                val newItem = newItems[indexInNewItemsList]
                if (item.hasToBeReplacedBy(newItem)) { // the item needs to be updated
                    updateItemAtPosition(newItem.castAsIn(), internalListIndex)
                }
                newItems.removeAt(indexInNewItemsList)
            }
        }

        newItems.forEach { add(it) }

        return this
    }

    /**
     * Removes an item from the adapter.
     *
     *
     * For the remove to work properly, all the items has to override the
     * [AdapterItem.equals] and [AdapterItem.hashCode] methods.
     * Check the example in [RecyclerAdapter.addOrUpdate]
     *
     * @param item item to remove
     * @return true if the item has been correctly removed or false if the item does not exist
     */
    fun removeItem(item: AdapterItem<*>): Boolean {
        val itemIndex = items.indexOf(item).takeIf { it >= 0 } ?: return false
        return removeItemAtPosition(itemIndex)
    }

    /**
     * Removes all the items with a certain class from this adapter and automatically notifies changes.
     *
     * @param clazz    class of the items to be removed
     * @param listener listener invoked for every item that is found. If the callback returns true,
     * the item will be removed. If it returns false, the item will not be removed
     */
    @JvmOverloads
    fun removeAllItemsWithClass(clazz: Class<out AdapterItem<*>>,
                                listener: RemoveListener = object : RemoveListener {
                                    override fun hasToBeRemoved(item: AdapterItem<*>) = true
                                }) {
        if (items.isEmpty())
            return

        val iterator = items.listIterator()
        var index: Int
        while (iterator.hasNext()) {
            index = iterator.nextIndex()
            val item = iterator.next()
            if (item.javaClass.name == clazz.name && listener.hasToBeRemoved(item)) {
                iterator.remove()
                notifyItemRemoved(index)
            }
        }

        //TODO: check for type removal in all the other remove methods if the last of a kind has been removed
        typeIds[clazz.name]?.let {
            typeIds.remove(clazz.name)
            types.remove(it)
        }
    }

    /**
     * Gets the last item with a given class, together with its position.
     *
     * @param clazz class of the item to search
     * @return Pair with position and AdapterItem or null if the adapter is empty or no items
     * exists with the given class
     */
    fun getLastItemWithClass(clazz: Class<out AdapterItem<*>>): Pair<Int, AdapterItem<*>>? {
        if (items.isEmpty())
            return null

        for (i in items.lastIndex downTo 0) {
            if (items[i].javaClass.name == clazz.name) {
                return Pair(i, items[i])
            }
        }

        return null
    }

    /**
     * Removes only the last item with a certain class from the adapter.
     *
     * @param clazz class of the item to remove
     */
    fun removeLastItemWithClass(clazz: Class<out AdapterItem<*>>) {
        items.takeIf { !it.isEmpty() }?.let { items ->
            for (i in items.lastIndex downTo 0) {
                if (items[i].javaClass.name == clazz.name) {
                    items.removeAt(i)
                    notifyItemRemoved(i)
                    break
                }
            }
        }
    }

    /**
     * Removes an item in a certain position. Does nothing if the adapter is empty or if the
     * position specified is out of adapter bounds.
     *
     * @param position position to be removed
     * @return true if the item has been removed, false if it doesn't exist or the position
     * is out of bounds
     */
    fun removeItemAtPosition(position: Int) =
            items.takeIf { !it.isEmpty() && !position.isOutOfItemsRange() }?.let { items ->
                items.removeAt(position)
                notifyItemRemoved(position)
                true
            } ?: false

    /**
     * Gets an item at a given position.
     *
     * @param position item position
     * @return [AdapterItem] or null if the adapter is empty or the position is out of bounds
     */
    fun getItemAtPosition(position: Int): AdapterItem<*>? =
            items.takeIf { !it.isEmpty() && !position.isOutOfItemsRange() }
                    ?.let { items -> items[position] }

    /**
     * Clears all the elements in the adapter.
     */
    fun clear() {
        items.let {
            val itemsSize = it.size
            it.clear()
            if (itemsSize > 0) {
                notifyItemRangeRemoved(0, itemsSize)
            }
        }
    }

    /**
     * Enables reordering of the list through drag and drop, which is activated when the user
     * long presses on an item.
     *
     * @param recyclerView recycler view on which to apply the drag and drop
     */
    fun enableDragDrop(recyclerView: RecyclerView) {
        val touchHelper = ItemTouchHelper(object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                return ItemTouchHelper.Callback.makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, DOWN or UP or START or END)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                val sourcePosition = viewHolder.adapterPosition
                val targetPosition = target.adapterPosition

                Collections.swap(items, sourcePosition, targetPosition)
                notifyItemMoved(sourcePosition, targetPosition)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //Do nothing here
            }
        })

        touchHelper.attachToRecyclerView(recyclerView)
    }

    /**
     * Filters this adapter with a given search term and shows only the items which
     * matches it.
     *
     *
     * For the filter to work properly, each item must override the
     * [AdapterItem.onFilter] method and provide custom implementation.
     *
     * @param searchTerm search term
     */
    fun filter(searchTerm: String?) {
        if (itemsList.isEmpty()) {
            return
        }

        if (searchTerm.isNullOrBlank()) {
            showFiltered = false
            notifyDataSetChanged()
            return
        }

        filtered.apply {
            clear()
            addAll(itemsList.filter { it.onFilter(searchTerm) })
        }

        showFiltered = true
        notifyDataSetChanged()

    }

    /**
     * Sort items.
     *
     * each item must override the [AdapterItem.compareTo] method.
     *
     * With this method you can override the default [AdapterItem.compareTo] and use a
     * custom comparator which is responsible of item comparison.
     *
     * This is useful when your items has to be sorted with many different strategies
     * and not just one (e.g. order items by name, by date, ...).
     *
     * @param ascending  true for ascending order (A-Z) or false for descending order (Z-A).
     * Ascending order follows the passed comparator sorting algorithm order,
     * descending order uses the inverse order
     * @param comparator custom comparator implementation
     */
    @Suppress("UNCHECKED_CAST")
    fun sort(ascending: Boolean, comparator: Comparator<AdapterItem<*>>? = null) {
        val items = (items as ArrayList<AdapterItem<*>>)
                .takeIf { !it.isEmpty() }
                ?: return

        if (ascending) {
            if (comparator == null) {
                items.sort()
            } else {
                items.sortWith(comparator)
            }
        } else {
            if (comparator == null) {
                items.sortDescending()
            } else {
                Collections.reverseOrder(comparator)
            }
        }

        notifyDataSetChanged()
    }

    /**
     * Sets the policy for a selection group.
     *
     * @param selectionGroup unique ID String of the selection group
     * @param multiSelect true if the user can select multiple items in this group, false if only
     * a single item can be selected at a time.
     */
    fun setSelectionGroupPolicy(selectionGroup: String, multiSelect: Boolean) {
        selectionGroups[selectionGroup] = multiSelect
    }

    /**
     * Gets the policy for a selection group.
     * New groups without a policy defaults to mutually exclusive single selection.
     *
     * @param selectionGroup unique ID String of the selection group
     * @return true if multiple items can be selected, false if mutually exclusive single selection
     */
    fun canSelectMultipleItems(selectionGroup: String): Boolean = selectionGroups[selectionGroup] ?: false

    /**
     * Gets a list of all the selected items in a selection group.
     *
     * @param selectionGroup unique ID String of the selection group
     * @return list of selected items
     */
    fun getSelectedItems(selectionGroup: String): List<AdapterItem<*>> {
        if (items.isEmpty())
            return emptyList()

        return items.filter { it.getSelectionGroup().equals(selectionGroup) && it.selected }
    }
}
