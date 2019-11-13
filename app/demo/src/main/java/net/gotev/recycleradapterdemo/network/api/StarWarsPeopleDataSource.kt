package net.gotev.recycleradapterdemo.network.api

import android.util.Log
import androidx.paging.PageKeyedDataSource
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.paging.withEmptyItem
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.TitleSubtitleItem


class StarWarsPeopleDataSource(private val api: StarWarsAPI) : PageKeyedDataSource<String, AdapterItem<*,*>>() {
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<String, AdapterItem<*,*>>) {
        val emptyItem = LabelItem("No items in the list")
        try {
            val response = api.getPeople().blockingGet()
            callback.withEmptyItem(
                    emptyItem,
                    response.results.map { convert(it) },
                    response.previous,
                    response.next
            )
        } catch (exc: Throwable) {
            Log.e("Error", "Error", exc)
            callback.withEmptyItem(emptyItem)
        }
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<String, AdapterItem<*,*>>) {
        load(params, callback)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<String, AdapterItem<*,*>>) {
        load(params, callback, isBefore = true)
    }

    private fun convert(model: SWAPIPerson): AdapterItem<*,*> {
        return TitleSubtitleItem(model.name, "Height (cm): ${model.height}")
    }

    private fun load(params: LoadParams<String>,
                     callback: LoadCallback<String, AdapterItem<*,*>>,
                     isBefore: Boolean = false) {
        try {
            val response = api.getPeopleFromUrl(params.key).blockingGet()
            callback.onResult(
                    response.results.map { convert(it) },
                    if (isBefore) response.previous else response.next
            )
        } catch (exc: Throwable) {
            Log.e("Error", "Error", exc)
            callback.onResult(emptyList(), null)
        }
    }
}
