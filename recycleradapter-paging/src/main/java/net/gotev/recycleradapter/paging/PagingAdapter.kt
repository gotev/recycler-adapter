package net.gotev.recycleradapter.paging

import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapterNotifier
import net.gotev.recycleradapter.RecyclerAdapterViewHolder
import net.gotev.recycleradapter.castAsIn
import net.gotev.recycleradapter.viewType

class PagingAdapter(
    dataSource: () -> DataSource<*, *>,
    config: PagedList.Config
) : PagedListAdapter<AdapterItem<*>, RecyclerAdapterViewHolder>(diffCallback),
    RecyclerAdapterNotifier {

    companion object {
        val diffCallback = object : DiffUtil.ItemCallback<AdapterItem<*>>() {
            override fun areItemsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: AdapterItem<*>, newItem: AdapterItem<*>) =
                !oldItem.hasToBeReplacedBy(oldItem)
        }
    }

    private val dataSourceFactory: DataSourceFactory<Any> = DataSourceFactory(dataSource)
    private val data = LivePagedListBuilder<Any, AdapterItem<*>>(dataSourceFactory, config).build()

    init {
        setHasStableIds(true)
    }

    fun startObserving(owner: LifecycleOwner, onLoadingComplete: (() -> Unit)? = null) {
        data.observe(owner, Observer {
            submitList(it)
            onLoadingComplete?.invoke()
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapterViewHolder {
        val item = currentList?.find { it.viewType() == viewType }
        require(item != null) { "onCreateViewHolder: cannot find a view with viewType $viewType Check the DataSource implementation!" }

        return item.createItemViewHolder(parent)
    }

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

    override fun getItemViewType(position: Int) =
        adapterItem(position, caller = "getItemViewType").viewType()

    override fun getItemId(position: Int) =
        adapterItem(position, caller = "getItemId").diffingId().hashCode().toLong()

    fun reload() {
        data.value?.dataSource?.invalidate()
    }

    private fun adapterItem(position: Int, caller: String): AdapterItem<*> {
        val item = getItem(position)
        require(item != null) { "$caller: no item found at position $position. Check the DataSource implementation!" }

        return item
    }

    private fun bindItem(holder: RecyclerAdapterViewHolder, position: Int, firstTime: Boolean) {
        val item = adapterItem(position, caller = "bindItem")

        holder.setAdapter(this)
        item.castAsIn().bind(firstTime, holder)
    }

    override fun getAdapterItem(holder: RecyclerAdapterViewHolder): AdapterItem<*>? {
        return getItem(holder.adapterPosition)
    }

    override fun notifyItemChanged(holder: RecyclerAdapterViewHolder) {
        // not supported
    }
}
