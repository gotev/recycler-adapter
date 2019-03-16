package net.gotev.recycleradapterdemo.paging

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.RecyclerAdapterViewHolder


private class PagedRecyclerAdapter
    : PagedListAdapter<AdapterItem<*>, RecyclerAdapterViewHolder>(diffCallback) {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AdapterItem<*>>() {
            override fun areItemsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                    oldItem == newItem

            override fun areContentsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                    !oldItem.hasToBeReplacedBy(oldItem)
        }
    }

    private val adapter = RecyclerAdapter()
    private var viewType: Int? = null

    override fun getItemViewType(position: Int) = viewType!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            adapter.onCreateViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) =
            (getItem(position) as AdapterItem<RecyclerAdapterViewHolder>).bind(holder)

    override fun submitList(pagedList: PagedList<AdapterItem<*>>?) {
        if (viewType == null) {
            pagedList?.firstOrNull()?.let {
                adapter.add(it)
                viewType = adapter.getItemViewType(adapter.lastItemIndex)
            }
        }
        super.submitList(pagedList)
    }
}

class PagingHelper<Key>(dataSource: () -> DataSource<Key, AdapterItem<*>>,
                        config: PagedList.Config) {
    private val dataSourceFactory = object : DataSource.Factory<Key, AdapterItem<*>>() {
        override fun create(): DataSource<Key, AdapterItem<*>> {
            return dataSource()
        }
    }

    private val liveData = LivePagedListBuilder(dataSourceFactory, config).build()
    private val adapter = PagedRecyclerAdapter()

    fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.adapter = adapter
    }

    fun start(lifecycleOwner: LifecycleOwner, action: (() -> Unit)? = null) {
        liveData.observe(lifecycleOwner, Observer {
            adapter.submitList(it)
            action?.invoke()
        })
    }

    fun reload() {
        liveData.value?.dataSource?.invalidate()
    }
}
