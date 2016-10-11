package net.gotev.recycleradapter;

/**
 * Listener invoked for every element that is going to be removed.
 * @author Aleksandar Gotev
 */

public interface RemoveListener {
    boolean hasToBeRemoved(AdapterItem item);
}
