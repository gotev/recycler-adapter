package net.gotev.recycleradapter.paging

import androidx.paging.DataSource
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.casted

internal class DataSourceFactory<T>(
    private val dataSource: () -> DataSource<*, *>
) : DataSource.Factory<T, AdapterItem<*>>() {

    override fun create(): DataSource<T, AdapterItem<*>> = dataSource().casted()
}