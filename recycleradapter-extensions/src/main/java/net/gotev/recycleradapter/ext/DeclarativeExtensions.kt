package net.gotev.recycleradapter.ext

import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import kotlin.collections.ArrayList

/**
 * @author Aleksandar Gotev
 */

typealias AdapterItems = ArrayList<AdapterItem<*>>

@Deprecated(
    message = "use renderableItems{ }.toAdapter() instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
fun createRecyclerAdapterWith(vararg items: AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        val filtered = items.filterNotNull()
        if (filtered.isNotEmpty()) {
            add(filtered)
        }
    }
}

@Deprecated(
    message = "use renderableItems{ }.toAdapter() instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
fun createRecyclerAdapterWith(list: List<AdapterItem<*>?>?): RecyclerAdapter {
    return RecyclerAdapter().apply {
        val filtered = list?.filterNotNull() ?: emptyList()
        if (filtered.isNotEmpty()) {
            add(filtered)
        }
    }
}

@Deprecated(
    message = "use renderableItems{ } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
fun listOfAdapterItems(vararg items: AdapterItem<*>?): AdapterItems {
    return if (items.isEmpty()) {
        ArrayList(1)
    } else {
        ArrayList(items.filterNotNull())
    }
}

@Deprecated(
    message = "use renderableItems{ +itemToAdd(..) } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
fun AdapterItems.adding(item: AdapterItem<*>?): AdapterItems {
    return if (item == null) {
        this
    } else {
        apply { add(item) }
    }
}

@Deprecated(
    message = "use renderableItems{ } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
fun adapterItems(vararg items: AdapterItem<*>?): AdapterItems {
    return ArrayList(listOfNotNull(*items))
}

@Deprecated(
    message = "use renderableItems{ } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
inline fun <T> Iterable<T>.mapItems(transform: (T) -> AdapterItem<*>?): Array<AdapterItem<*>> {
    return mapNotNull(transform).toTypedArray()
}

@Deprecated(
    message = "use renderableItems{ map.forEach{ (key, value) -> +item(..) } } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
inline fun <K, V> Map<out K, V>.mapToManyAdapterItems(transform: (Map.Entry<K, V>) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

@Deprecated(
    message = "use renderableItems{ array.forEach{ +item(..) } } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
inline fun <T> Array<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

@Deprecated(
    message = "use renderableItems{ iterable.forEach{ +item(..) } } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
inline fun <T> Iterable<T>.mapToManyAdapterItems(transform: (T) -> List<AdapterItem<*>>): Array<AdapterItem<*>> {
    return map(transform).flatten().toTypedArray()
}

@Deprecated(
    message = "use renderableItems{ }.toAdapter() instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
@Suppress("DEPRECATION")
inline fun <T> Iterable<T>.createRecyclerAdapterByMapping(transform: (T) -> AdapterItem<*>?): RecyclerAdapter {
    return RecyclerAdapter().add(mapToAdapterItems(transform))
}

@Deprecated(
    message = "use renderableItems{ iterable.forEach{ +item(..) } } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
inline fun <T> Iterable<T>.mapToAdapterItems(transform: (T) -> AdapterItem<*>?): AdapterItems {
    return ArrayList(mapNotNull(transform))
}

@Deprecated(
    message = "use renderableItems{ array.forEach{ +item(..) } } instead",
    level = DeprecationLevel.WARNING,
    replaceWith = ReplaceWith("renderableItems")
)
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

    fun render(renderableItems: RenderableItems) {
        recyclerAdapter.syncWithItems(renderableItems.items)
    }

    fun render(action: RenderableItems.() -> Unit) {
        recyclerAdapter.syncWithItems(renderableItems(action).items)
    }

    @Deprecated(
        message = "use render(renderableItems) instead",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("render(renderableItems)")
    )
    fun AdapterItems.render() {
        recyclerAdapter.syncWithItems(this)
    }

    @Deprecated(
        message = "use render(renderableItems) instead",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("render(renderableItems)")
    )
    fun render(vararg items: AdapterItem<*>?) {
        renderList(items.filterNotNull())
    }

    @Deprecated(
        message = "use render(renderableItems) instead",
        level = DeprecationLevel.WARNING,
        replaceWith = ReplaceWith("render(renderableItems)")
    )
    fun render(list: List<AdapterItem<*>?>?) {
        renderList(list?.filterNotNull() ?: emptyList())
    }

    @Suppress("DEPRECATION")
    private fun renderList(list: List<AdapterItem<*>>) {
        if (list.isEmpty()) {
            recyclerAdapter.clear()
        } else {
            ArrayList(list).render()
        }
    }
}
