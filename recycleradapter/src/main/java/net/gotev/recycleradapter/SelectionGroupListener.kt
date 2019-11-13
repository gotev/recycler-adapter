package net.gotev.recycleradapter

/**
 * Listener for changes of selected items in a selection group.
 *
 * @author Aleksandar Gotev
 */
typealias SelectionGroupListener = (selectionGroup: String, selected: List<AdapterItem<*>>) -> Unit
