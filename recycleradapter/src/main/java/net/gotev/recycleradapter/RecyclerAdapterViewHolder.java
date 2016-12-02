package net.gotev.recycleradapter;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Base ViewHolder class to extend in subclasses.
 * @author Aleksandar Gotev
 */
public abstract class RecyclerAdapterViewHolder extends RecyclerView.ViewHolder {

    private final WeakReference<RecyclerAdapterNotifier> adapter;

    public RecyclerAdapterViewHolder(View itemView, RecyclerAdapterNotifier adapter) {
        super(itemView);
        this.adapter = new WeakReference<>(adapter);
    }

    /**
     * Sends an event to the adapter.
     * @param data additional event data
     */
    protected final void sendEvent(Bundle data) {
        this.adapter.get().sendEvent(this, data);
    }

    protected final View findViewById(int id) {
        return itemView.findViewById(id);
    }
}
