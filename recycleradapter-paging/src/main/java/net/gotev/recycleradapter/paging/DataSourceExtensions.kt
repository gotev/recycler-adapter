package net.gotev.recycleradapter.paging

import androidx.paging.ItemKeyedDataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PositionalDataSource
import net.gotev.recycleradapter.AdapterItem

fun <E> PageKeyedDataSource.LoadInitialCallback<E, AdapterItem<*, *>>.withEmptyItem(
        emptyItem: AdapterItem<*, *>,
        data: List<AdapterItem<*, *>> = emptyList(),
        previousPageKey: E? = null,
        nextPageKey: E? = null
) {
    onResult(data.empty(emptyItem), previousPageKey, nextPageKey)
}

fun <E> PageKeyedDataSource.LoadInitialCallback<E, AdapterItem<*, *>>.withEmptyItem(
        emptyItem: AdapterItem<*, *>,
        data: List<AdapterItem<*, *>> = emptyList(),
        position: Int,
        totalCount: Int,
        previousPageKey: E? = null,
        nextPageKey: E? = null
) {
    onResult(data.empty(emptyItem), position, totalCount, previousPageKey, nextPageKey)
}

fun ItemKeyedDataSource.LoadInitialCallback<AdapterItem<*, *>>.withEmptyItem(
        emptyItem: AdapterItem<*, *>,
        data: List<AdapterItem<*, *>> = emptyList(),
        position: Int,
        totalCount: Int
) {
    onResult(data.empty(emptyItem), position, totalCount)
}

fun PositionalDataSource.LoadInitialCallback<AdapterItem<*, *>>.withEmptyItem(
        emptyItem: AdapterItem<*, *>,
        data: List<AdapterItem<*, *>> = emptyList(),
        position: Int,
        totalCount: Int
) {
    onResult(data.empty(emptyItem), position, totalCount)
}

fun PositionalDataSource.LoadInitialCallback<AdapterItem<*, *>>.withEmptyItem(
        emptyItem: AdapterItem<*, *>,
        data: List<AdapterItem<*, *>> = emptyList(),
        position: Int
) {
    onResult(data.empty(emptyItem), position)
}

private fun List<AdapterItem<*, *>>.empty(emptyItem: AdapterItem<*, *>) =
        takeIf { isNotEmpty() } ?: listOf(emptyItem)