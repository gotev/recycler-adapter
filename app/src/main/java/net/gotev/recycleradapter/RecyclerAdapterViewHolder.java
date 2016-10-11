package net.gotev.recycleradapter;

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
     * Gets the adapter in which this ViewHolder is contained.
     * Used to notify the adapter that something has changed in the data model.
     * @return {@link RecyclerAdapterNotifier} instance or null if the parent adapter has
     * already been disposed
     */
    protected RecyclerAdapterNotifier getAdapter() {
        return adapter.get();
    }
}
