package net.gotev.recycleradapter;

import android.os.Bundle;


/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
public interface RecyclerAdapterNotifier {
    void sendEvent(RecyclerAdapterViewHolder holder, Bundle data);
}
