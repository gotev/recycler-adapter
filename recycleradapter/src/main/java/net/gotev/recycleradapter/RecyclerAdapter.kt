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

/**
 * Helper class to easily work with Android's RecyclerView.Adapter.
 *
 * @author Aleksandar Gotev
 */
class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapterViewHolder>(), RecyclerAdapterNotifier {

    private val typeIds = LinkedHashMap<String, Int>()
    private val types = LinkedHashMap<Int, AdapterItem<*>>()
    private val itemsList = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()
    private var emptyItem: AdapterItem<in RecyclerAdapterViewHolder>? = null
    private var emptyItemId: Int = 0

    private var filtered = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()
    private var showFiltered: Boolean = false

    private val items: ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>
        get() = if (showFiltered) filtered else itemsList

    /**
     * Sets the item to show when the recycler adapter is empty.
     *
     * @param item item to show when the recycler adapter is empty
     */
    fun setEmptyItem(item: AdapterItem<out RecyclerAdapterViewHolder>) {
        emptyItem = item as AdapterItem<in RecyclerAdapterViewHolder>
        emptyItemId = View.generateViewId()

        if (items.isEmpty())
            notifyItemInserted(0)
    }

    /**
     * Adds a new item to this adapter
     *
     * @param item item to add
     * @return [RecyclerAdapter]
     */
    fun add(item: AdapterItem<*>): RecyclerAdapter {
        registerItemType(item)
        items.add(item as AdapterItem<in RecyclerAdapterViewHolder>)
        removeEmptyItemIfItHasBeenConfigured()

        notifyItemInserted(items.size - 1)
        return this
    }

    /**
     * Gets the position of an item in an adapter.
     *
     *
     * For the method to work properly, all the items has to override the
     * [AdapterItem.equals] and [AdapterItem.hashCode] methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item (plus some other changes). Check the example in [RecyclerAdapter.add]
     *
     * @param item item object
     * @return the item's position or -1 if the item does not exist
     */
    fun getItemPosition(item: AdapterItem<*>): Int {
        return items.indexOf(item)
    }

    /**
     * Adds an item into the adapter or updates it if already existing.
     *
     *
     * For the update to work properly, all the items has to override the
     * [AdapterItem.equals] and [AdapterItem.hashCode] methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item (plus some other changes).
     *
     *
     * As an example consider the following item
     * (written in pseudocode, to write less code):
     * <pre>
     * Person extends AdapterItem {
     * String id;
     * String name;
     * String city;
     * }
    </pre> *
     * in this case every person is uniquely identified by its id, while other data may change, so
     * the [AdapterItem.equals] method will look like this:
     *
     *
     * <pre>
     * public boolean equals(Object obj) {
     * if (this == obj) {
     * return true;
     * }
     *
     * if (obj == null || getClass() != obj.getClass()) {
     * return false;
     * }
     *
     * Person other = (Person) obj;
     * return other.getId().equals(id);
     * }
    </pre> *
     *
     *
     * If the item already exists in the list, by impementing
     * [AdapterItem.hasToBeReplacedBy] in your AdapterItem, you can decide
     * when the new item should replace the existing one in the list, reducing the workload of
     * the recycler view.
     *
     *
     * Check hasToBeReplacedBy method JavaDoc for more information.
     *
     * @param item item to add or update
     * @return [RecyclerAdapter]
     */
    fun addOrUpdate(item: AdapterItem<in RecyclerAdapterViewHolder>): RecyclerAdapter {
        val itemIndex = getItemPosition(item)

        if (itemIndex < 0) {
            return add(item)
        }

        val internalItem = items[itemIndex]
        if (internalItem.hasToBeReplacedBy(item)) { // the item needs to be updated
            updateItemAtPosition(item, itemIndex)
        }

        return this
    }

    private fun updateItemAtPosition(item: AdapterItem<in RecyclerAdapterViewHolder>, position: Int) {
        items[position] = item
        notifyItemChanged(position)
    }

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
    fun syncWithItems(newItems: ArrayList<out AdapterItem<out RecyclerAdapterViewHolder>>?): RecyclerAdapter {
        if (newItems == null || newItems.isEmpty()) {
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
                    updateItemAtPosition(newItem as AdapterItem<in RecyclerAdapterViewHolder>, internalListIndex)
                }
                newItems.removeAt(indexInNewItemsList)
            }
        }

        for (newItem in newItems) {
            add(newItem)
        }

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
        val itemIndex = items.indexOf(item)

        return if (itemIndex < 0) {
            false
        } else removeItemAtPosition(itemIndex)

    }

    /**
     * Adds a new item to this adapter
     *
     * @param item     item to add
     * @param position position at which to add the element. The item previously at
     * (position) will be at (position + 1) and the same for all the subsequent
     * elements
     * @return [RecyclerAdapter]
     */
    fun addAtPosition(item: AdapterItem<in RecyclerAdapterViewHolder>, position: Int): RecyclerAdapter {
        registerItemType(item)
        items.add(position, item)
        removeEmptyItemIfItHasBeenConfigured()

        notifyItemInserted(position)
        return this
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

        val item = items[position]
        val className = item.javaClass.name
        return typeIds[className]!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterViewHolder {
        val item = if (adapterIsEmptyAndEmptyItemIsDefined() && viewType == emptyItemId) {
            emptyItem
        } else {
            types[viewType]
        }!!

        try {
            val ctx = parent.context
            val view = LayoutInflater.from(ctx).inflate(item.getLayoutId(), parent, false)
            return item.getViewHolder(view, this)

        } catch (exc: NoSuchMethodException) {
            throw RuntimeException("${javaClass.simpleName} - onCreateViewHolder error: you should declare " +
                    "a constructor like this in your ViewHolder: " +
                    "public RecyclerAdapterViewHolder(View itemView, RecyclerAdapterNotifier adapter)", exc)

        } catch (exc: IllegalAccessException) {
            throw RuntimeException("${javaClass.simpleName} - Your ViewHolder class in " +
                    item.javaClass.name + " should be public!", exc)

        } catch (exc: Throwable) {
            throw RuntimeException("${javaClass.simpleName} - onCreateViewHolder error", exc)
        }

    }

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        if (adapterIsEmptyAndEmptyItemIsDefined()) {
            emptyItem?.bind(holder)
        } else {
            items[position].bind(holder)
        }
    }

    override fun getItemCount(): Int {
        return if (adapterIsEmptyAndEmptyItemIsDefined()) 1 else items.size

    }

    override fun sendEvent(holder: RecyclerAdapterViewHolder, data: Bundle?) {
        val position = holder.adapterPosition

        if (position < 0 || position >= items.size)
            return

        if (items[position].onEvent(position, data))
            notifyItemChanged(position)
    }

    /**
     * Removes all the items with a certain class from this adapter and automatically notifies changes.
     *
     * @param clazz    class of the items to be removed
     * @param listener listener invoked for every item that is found. If the callback returns true,
     * the item will be removed. If it returns false, the item will not be removed
     */
    @JvmOverloads
    fun removeAllItemsWithClass(clazz: Class<out AdapterItem<*>>?, listener: RemoveListener? = object : RemoveListener {
        override fun hasToBeRemoved(item: AdapterItem<*>): Boolean {
            return true
        }
    }) {
        if (clazz == null)
            throw IllegalArgumentException("The class of the items can't be null!")

        if (listener == null)
            throw IllegalArgumentException("RemoveListener can't be null!")

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

        val id = typeIds[clazz.name]
        if (id != null) {
            typeIds.remove(clazz.name)
            types.remove(id)
        }
    }

    /**
     * Gets the last item with a given class, together with its position.
     *
     * @param clazz class of the item to search
     * @return Pair with position and AdapterItem or null if the adapter is empty or no items
     * exists with the given class
     */
    fun getLastItemWithClass(clazz: Class<out AdapterItem<*>>?): Pair<Int, AdapterItem<*>>? {
        if (clazz == null)
            throw IllegalArgumentException("The class of the items can't be null!")

        if (items.isEmpty())
            return null

        for (i in items.size - 1 downTo 0) {
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
        if (items.isEmpty())
            return

        for (i in items.size - 1 downTo 0) {
            if (items[i].javaClass.name == clazz.name) {
                items.removeAt(i)
                notifyItemRemoved(i)
                break
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
    fun removeItemAtPosition(position: Int): Boolean {
        if (items.isEmpty() || position < 0 || position >= items.size)
            return false

        items.removeAt(position)
        notifyItemRemoved(position)

        return true
    }

    /**
     * Gets an item at a given position.
     *
     * @param position item position
     * @return [AdapterItem] or null if the adapter is empty or the position is out of bounds
     */
    fun getItemAtPosition(position: Int): AdapterItem<*>? {
        return if (items.isEmpty() || position < 0 || position >= items.size) null else items[position]

    }

    /**
     * Clears all the elements in the adapter.
     */
    fun clear() {
        val itemsSize = items.size
        items.clear()
        if (itemsSize > 0) {
            notifyItemRangeRemoved(0, itemsSize)
        }
    }

    private fun adapterIsEmptyAndEmptyItemIsDefined(): Boolean {
        return items.isEmpty() && emptyItem != null
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

        if (searchTerm == null || searchTerm.isEmpty()) {
            showFiltered = false
            notifyDataSetChanged()
            return
        }

        filtered.clear()
        filtered.addAll(itemsList.filter { it.onFilter(searchTerm) })

        showFiltered = true
        notifyDataSetChanged()

    }

    /**
     * Sort items.
     *
     *
     * For this method to work properly, each item must override the
     * [AdapterItem.compareTo] method.
     *
     * @param ascending true for ascending order (A-Z) or false for descending order (Z-A)
     */
    fun sort(ascending: Boolean) {
        val items = items

        if (items == null || items.isEmpty())
            return

        if (ascending) {
            (items as ArrayList<AdapterItem<out RecyclerAdapterViewHolder>>).sort()
        } else {
            (items as ArrayList<AdapterItem<out RecyclerAdapterViewHolder>>).sortDescending()
        }

        notifyDataSetChanged()
    }

    /**
     * Sort items.
     *
     *
     * With this method, the items doesn't have to override the
     * [AdapterItem.compareTo] method, as the comparator is passed as
     * argument and is responsible of item comparison. You can use this sort method if your items
     * has to be sorted with many different strategies and not just one
     * (e.g. order items by name, by date, ...).
     *
     * @param ascending  true for ascending order (A-Z) or false for descending order (Z-A).
     * Ascending order follows the passed comparator sorting algorithm order,
     * descending order uses the inverse order
     * @param comparator custom comparator implementation
     */
    fun sort(ascending: Boolean, comparator: Comparator<AdapterItem<*>>) {
        val items = items

        if (items == null || items.isEmpty())
            return

        if (ascending) {
            (items as ArrayList<AdapterItem<out RecyclerAdapterViewHolder>>).sortedWith(comparator)
        } else {
            (items as ArrayList<AdapterItem<out RecyclerAdapterViewHolder>>).sortedWith(comparator).reversed()
        }

        notifyDataSetChanged()
    }

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
}
/**
 * Removes all the items with a certain class from this adapter and automatically notifies changes.
 *
 * @param clazz class of the items to be removed
 */
