package net.gotev.recycleradapterdemo.adapteritems

import android.content.Context

import net.gotev.recycleradapter.AdapterItem

class SyncItem(context: Context, val id: Int, private val suffix: String) : ExampleItem(context, "item $id $suffix") {

    override fun equals(other: Any?): Boolean {
        if (other == null || javaClass != other.javaClass) {
            return false
        }

        return id == (other as SyncItem).id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun compareTo(other: AdapterItem<*>): Int {
        if (other.javaClass != javaClass)
            return -1

        val item = other as SyncItem

        if (id == item.id)
            return 0

        return if (id > item.id) 1 else -1
    }

    override fun hasToBeReplacedBy(newItem: AdapterItem<*>): Boolean {
        val otherItem = newItem as SyncItem
        return otherItem.id + suffix.hashCode() != id + suffix.hashCode()
    }
}
