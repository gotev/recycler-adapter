package net.gotev.recycleradapter

/**
 * Listener invoked for every element that is going to be removed.
 * @author Aleksandar Gotev
 */

interface RemoveListener {
    fun hasToBeRemoved(item: AdapterItem<*, *>): Boolean
}
