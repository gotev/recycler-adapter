package net.gotev.recycleradapter.paging

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.NO_ID
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapter.castAsIn
import net.gotev.recycleradapter.viewType

class PagingAdapter(
    owner: LifecycleOwner,
    dataSource: () -> DataSource<*, *>,
    config: PagedList.Config,
    onLoadingComplete: (() -> Unit)? = null
) : PagedListAdapter<AdapterItem<*>, RecyclerAdapterViewHolder>(diffCallback) {

    private var dataSourceFactory: DataSourceFactory<Any> = DataSourceFactory(dataSource)

    private var data: LiveData<PagedList<AdapterItem<*>>>

    init {
        data = LivePagedListBuilder<Any, AdapterItem<*>>(dataSourceFactory, config).build()

        data.observe(owner, Observer {
            submitList(it)
            onLoadingComplete?.invoke()
        })

        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = currentList
        ?.find { it.viewType() == viewType }
        ?.createItemViewHolder(parent)
        ?: throw IllegalStateException("Item not found")

    override fun onBindViewHolder(holder: RecyclerAdapterViewHolder, position: Int) {
        bindItem(holder, position, true)
    }

    override fun onBindViewHolder(
        holder: RecyclerAdapterViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        bindItem(holder, position, payloads.isEmpty())
    }

    override fun getItemViewType(position: Int) = getItem(position).viewType()

    override fun getItemId(position: Int) =
        getItem(position)?.diffingId()?.hashCode()?.toLong() ?: NO_ID

    fun reload() {
        data.value?.dataSource?.invalidate()
    }

    private fun bindItem(holder: RecyclerAdapterViewHolder, position: Int, firstTime: Boolean) {
        getItem(position)?.castAsIn()?.bind(firstTime, holder)
            ?: throw IllegalStateException("Item not found")
    }

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AdapterItem<*>>() {
            override fun areItemsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                !oldItem.hasToBeReplacedBy(oldItem)
        }
    }
}