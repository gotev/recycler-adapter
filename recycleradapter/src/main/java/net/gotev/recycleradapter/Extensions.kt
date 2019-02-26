package net.gotev.recycleradapter

/**
 * @author Aleksandar Gotev
 */
internal fun AdapterItem<*>?.viewType() = this?.javaClass?.name?.hashCode() ?: 0
internal fun Class<out AdapterItem<*>>.viewType() = hashCode()
