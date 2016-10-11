package net.gotev.recycleradapter;

import android.os.Bundle;

/**
 * Created by Aleksandar Gotev (aleksandar@igenius.net) on 10/10/16.
 */

public interface RecyclerAdapterNotifier {
    void notifyItemChanged(RecyclerAdapterViewHolder holder, Bundle dataChanged);
}
