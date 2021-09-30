package net.gotev.recycleradapter.ext

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import kotlin.collections.ArrayList

class RenderableItems internal constructor() : Iterable<AdapterItem<*>> {
    internal val items = ArrayList<AdapterItem<*>>()

    operator fun AdapterItem<*>?.unaryPlus() {
        if (this != null) {
            items.add(this)
        }
    }

    operator fun Array<out AdapterItem<*>?>?.unaryPlus() {
        this?.forEach { item ->
            if (item != null) {
                items.add(item)
            }
        }
    }

    operator fun Iterable<AdapterItem<*>?>?.unaryPlus() {
        this?.forEach { item ->
            if (item != null) {
                items.add(item)
            }
        }
    }

    override fun iterator(): Iterator<AdapterItem<*>> = items.iterator()

    fun toAdapter(): RecyclerAdapter = RecyclerAdapter().apply { add(items) }
}

fun renderableItems(action: RenderableItems.() -> Unit): RenderableItems {
    val builder = RenderableItems()
    action(builder)
    return builder
}

interface RecyclerAdapterProvider {
    val recyclerAdapter: RecyclerAdapter

    /**
     * Renders items in the recycler view.
     * @param canvas renderable items to display in the list
     */
    fun render(canvas: RenderableItems) {
        syncItemsOrEmpty(null, canvas)
    }

    /**
     * Renders items in the recycler view.
     * @param onEmptyCanvas renderable items to be displayed if and only if [canvas]
     * renderable items are empty. null by default.
     * @param canvas renderable items to display in the list
     */
    fun render(onEmptyCanvas: RenderableItems? = null, canvas: RenderableItems) {
        syncItemsOrEmpty(onEmptyCanvas, canvas)
    }

    /**
     * Renders items in the recycler view.
     * @param onEmptyCanvas renderable items to be displayed if and only if [canvas]
     * renderable items are empty. null by default.
     * @param canvas renderable items to display in the list
     */
    fun render(onEmptyCanvas: RenderableItems? = null, canvas: RenderableItems.() -> Unit) {
        syncItemsOrEmpty(onEmptyCanvas, renderableItems(canvas))
    }

    private fun syncItemsOrEmpty(onEmptyCanvas: RenderableItems?, canvas: RenderableItems) {
        val items = canvas.items

        if (items.isNotEmpty())
            recyclerAdapter.syncWithItems(items)
        else
            onEmptyCanvas?.let { recyclerAdapter.syncWithItems(it.items) } ?: recyclerAdapter.clear()
    }
}
