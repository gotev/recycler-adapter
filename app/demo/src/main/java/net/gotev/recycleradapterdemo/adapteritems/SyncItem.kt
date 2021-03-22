package net.gotev.recycleradapterdemo.adapteritems

import net.gotev.recycleradapter.AdapterItem

class SyncItem(val id: Int, private val suffix: String) : TitleSubtitleItem("item $id $suffix", suffix) {

    override fun diffingId() = javaClass.name + id

    override fun compareTo(other: AdapterItem<*>): Int {
        if (other.javaClass != javaClass)
            return -1

        val item = other as SyncItem

        if (id == item.id)
            return 0

        return if (id > item.id) 1 else -1
    }

    override fun hasToBeReplacedBy(newItem: AdapterItem<*>): Boolean {
        if (newItem !is SyncItem)
            return true

        return suffix != newItem.suffix
    }
}
