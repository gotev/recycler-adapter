package net.gotev.recycleradapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.util.ArrayList
import java.util.Collections

/**
 * Makes the use of RecyclerView easier, modular and less error-prone
 *
 * @author Aleksandar Gotev
 */
class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapterViewHolder>(), RecyclerAdapterNotifier {

    private class Differ(
        private val oldList: List<AdapterItem<*>>,
        private val newList: List<AdapterItem<*>>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            !oldList[oldItemPosition].hasToBeReplacedBy(newList[newItemPosition])

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int) = true
    }

    private val itemsList = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()
    private val types = LinkedHashMap<Int, AdapterItem<*>>()

    private var filtered = ArrayList<AdapterItem<in RecyclerAdapterViewHolder>>()
    private var showFiltered = false

    private val items
        get() = if (showFiltered) filtered else itemsList

    init {
        setHasStableIds(true)
    }

    private fun notifyChangedPosition(position: Int) {
        notifyItemChanged(position, true)
    }

    /**
     * Returns a copy of the internal adapter item's list.
     */
    val adapterItems: ArrayList<AdapterItem<*>>
        get() = ArrayList(itemsList)

    private fun Int.isOutOfItemsRange() = this < 0 || this >= items.size

    private fun updateItemAtPosition(
        item: AdapterItem<in RecyclerAdapterViewHolder>,
        position: Int
    ) {
        items[position] = item
        notifyChangedPosition(position)
    }

    private fun registerItemType(item: AdapterItem<*>) {
        val classType = item.viewType()

        if (!types.containsKey(classType)) {
            types[classType] = item
        }
    }

    override fun getItemViewType(position: Int): Int = items[position].viewType()

    override fun getItemId(position: Int) = items[position].diffingId().hashCode().toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterViewHolder {
        val item = types.getValue(viewType)
        return item.createItemViewHolder(parent)
    }

    private fun bindItem(holder: RecyclerAdapterViewHolder, position: Int, firstTime: Boolean) {
        val item = items[position]
        holder.setAdapter(this)
        item.bind(firstTime, holder)
    }

    override fun onBindViewHolder(
        holder: RecyclerAdapterViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        bindItem(holder, position, payloads.isEmpty())
    }

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        bindItem(holder, position, true)
    }

    override fun onViewRecycled(holder: RecyclerAdapterViewHolder) {
        super.onViewRecycled(holder)
        holder.prepareForReuse()
    }

    override fun getItemCount() = items.size

    override fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>? {
        val position =
            holder.bindingAdapterPosition.takeIf { !it.isOutOfItemsRange() } ?: return null

        return items[position]
    }

    override fun notifyItemChanged(holder: RecyclerAdapterViewHolder) {
        val position = holder.bindingAdapterPosition.takeIf { !it.isOutOfItemsRange() } ?: return
        notifyChangedPosition(position)
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
            when {
                position >= items.size -> {
                    items.add(item.castAsIn())
                    items.lastIndex
                }

                position < 0 -> {
                    items.add(0, item.castAsIn())
                    0
                }

                else -> {
                    items.add(position, item.castAsIn())
                    position
                }
            }
        } else {
            items.add(item.castAsIn())
            items.lastIndex
        }

        registerItemType(item)
        notifyItemInserted(insertPosition)
        return this
    }

    /**
     * Adds many items to this adapter.
     *
     * This method has better performance when inserting many items if compared to multiple calls
     * of the single [add] method.
     *
     * @param newItems items to add
     * @param startingPosition position at which to start adding new elements. If null, the elements
     * will be added at the end of the list, otherwise the items will be inserted starting from
     * (startingPosition) and all the existing items starting from (startingPosition) will be
     * shifted forward.
     * @return [RecyclerAdapter]
     */
    fun add(newItems: List<AdapterItem<*>>, startingPosition: Int? = null): RecyclerAdapter {
        if (newItems.isEmpty()) return this

        val firstIndex = items.size

        if (startingPosition == null) {
            items.ensureCapacity(items.size + newItems.size)
            newItems.forEach {
                registerItemType(it)
                items.add(it.castAsIn())
            }
        } else {
            newItems.reversed().forEach {
                registerItemType(it)
                items.add(startingPosition, it.castAsIn())
            }
        }

        notifyItemRangeInserted(firstIndex, newItems.size)
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
    fun addOrUpdate(item: AdapterItem<*>): RecyclerAdapter {
        val itemIndex = items.indexOf(item).takeIf { it >= 0 } ?: return add(item)

        if (items[itemIndex].hasToBeReplacedBy(item)) {
            updateItemAtPosition(item.castAsIn(), itemIndex)
        }

        return this
    }

    /**
     * Adds or updates many items to this adapter.
     * Check [addOrUpdate] for more detailed information
     *
     * @param items items to add
     * @return [RecyclerAdapter]
     */
    fun addOrUpdate(items: List<AdapterItem<*>>): RecyclerAdapter {
        items.forEach { addOrUpdate(it) }
        return this
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
    fun syncWithItems(newItems: List<AdapterItem<*>>): RecyclerAdapter {
        if (newItems.isEmpty()) {
            clear()
            return this
        }

        val diffResult = DiffUtil.calculateDiff(Differ(oldList = items, newList = newItems))

        items.clear()
        types.clear()

        newItems.forEach {
            val newItem = it.castAsIn()
            registerItemType(newItem)
            items.add(newItem)
        }

        diffResult.dispatchUpdatesTo(this)

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
     * Removes an item in a certain position. Does nothing if the adapter is empty or if the
     * position specified is out of adapter bounds.
     *
     * @param position position to be removed
     * @return true if the item has been removed, false if it doesn't exist or the position
     * is out of bounds
     */
    fun removeItemAtPosition(position: Int) =
        items.takeIf { it.isNotEmpty() && !position.isOutOfItemsRange() }?.let { items ->
            items.removeAt(position)
            notifyItemRemoved(position)
            true
        } ?: false

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
     * Swaps the position of two Adapter Items in the list and updates the rendering.
     *
     * @throws IndexOutOfBoundsException if either [sourcePosition] or [targetPosition] are
     * out of bounds
     */
    fun swap(sourcePosition: Int, targetPosition: Int) {
        Collections.swap(items, sourcePosition, targetPosition)
        notifyItemMoved(sourcePosition, targetPosition)
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
}
