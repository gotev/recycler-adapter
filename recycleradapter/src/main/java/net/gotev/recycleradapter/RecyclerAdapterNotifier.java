package net.gotev.recycleradapter;

import android.os.Bundle;


/**
 * Contains methods to notify the adapter.
 * @author Aleksandar Gotev
 */
public interface RecyclerAdapterNotifier {
    void notifyItemChanged(RecyclerAdapterViewHolder holder, Bundle dataChanged);
}
