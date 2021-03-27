package net.gotev.recycleradapter.ext

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import kotlin.collections.ArrayList

/**
 * @author Aleksandar Gotev
 */

typealias AdapterItems = ArrayList<AdapterItem<*>>

fun createRecyclerAdapterWith(vararg items: AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        val filtered = items.filterNotNull()
        if (filtered.isNotEmpty()) {
            add(filtered)
        }
    }
}

fun createRecyclerAdapterWith(list: List<AdapterItem<*>?>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        val filtered = list?.filterNotNull() ?: emptyList()
        if (filtered.isNotEmpty()) {
            add(filtered)
        }
    }
}

fun listOfAdapterItems(vararg items: AdapterItem<*>?): AdapterItems {
    return if (items.isEmpty()) {
        ArrayList(1)
    } else {
        ArrayList(items.filterNotNull())
    }
}

fun AdapterItems.adding(item: AdapterItem<*>?): AdapterItems {
    return if (item == null) {
        this
    } else {
        apply { add(item) }
    }
}

fun adapterItems(vararg items: AdapterItem<*>?): AdapterItems {
    return ArrayList(listOfNotNull(*items))
}

inline fun <T> Iterable<T>.mapItems(transform: (T) -> AdapterItem<*>?): Array<AdapterItem<*>> {
    return mapNotNull(transform).toTypedArray()
}

inline fun <K, V> Map<out K, V>.mapToManyAdapterItems(transform: (Map.Entry<K, V>) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Array<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Iterable<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

inline fun <T> Iterable<T>.createRecyclerAdapterByMapping(transform: (T) -> AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().add(mapToAdapterItems(transform))
}

inline fun <T> Iterable<T>.mapToAdapterItems(transform: (T) -> AdapterItem<*>?): AdapterItems {
    return ArrayList(mapNotNull(transform))
}

inline fun <T> Array<T>.mapToAdapterItems(transform: (T) -> AdapterItem<*>?): AdapterItems {
    return ArrayList(mapNotNull(transform))
}

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

    fun AdapterItems.render() {
        recyclerAdapter.syncWithItems(this)
    }

    fun render(renderableItems: RenderableItems) {
        recyclerAdapter.syncWithItems(renderableItems.items)
    }

    fun render(action: RenderableItems.() -> Unit) {
        val builder = RenderableItems()
        action(builder)
        recyclerAdapter.syncWithItems(builder.items)
    }

    fun render(vararg items: AdapterItem<*>?) {
        renderList(items.filterNotNull())
    }

    fun render(list: List<AdapterItem<*>?>?) {
        renderList(list?.filterNotNull() ?: emptyList())
    }

    private fun renderList(list: List<AdapterItem<*>>) {
        if (list.isEmpty()) {
            recyclerAdapter.clear()
        } else {
            ArrayList(list).render()
        }
    }
}
