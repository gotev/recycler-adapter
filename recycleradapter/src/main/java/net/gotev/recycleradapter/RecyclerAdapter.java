package net.gotev.recycleradapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Helper class to easily work with Android's RecyclerView.Adapter.
 * @author Aleksandar Gotev
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapterViewHolder>
        implements RecyclerAdapterNotifier{

    private LinkedHashMap<String, Integer> typeIds;
    private LinkedHashMap<Integer, AdapterItem> types;
    private List<AdapterItem> items;

    /**
     * Applies swipe gesture detection on a RecyclerView items.
     * @param recyclerView recycler view o which to apply the swipe gesture
     * @param listener listener called when a swipe is performed on one of the items
     */
    public static void applySwipeGesture(RecyclerView recyclerView, final SwipeListener listener) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                listener.onItemSwiped(viewHolder.getAdapterPosition(), swipeDir);
            }
        }).attachToRecyclerView(recyclerView);
    }

    /**
     * Creates a new recyclerAdapter
     */
    public RecyclerAdapter() {
        typeIds = new LinkedHashMap<>();
        types = new LinkedHashMap<>();
        items = new ArrayList<>();
    }

    /**
     * Adds a new item to this adapter
     * @param item item to add
     * @return {@link RecyclerAdapter}
     */
    public RecyclerAdapter add(AdapterItem item) {
        String className = item.getClass().getName();

        if (!typeIds.containsKey(className)) {
            int viewId = ViewIdGenerator.generateViewId();
            typeIds.put(className, viewId);
            types.put(viewId, item);
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
        return this;
    }

    @Override
    public int getItemViewType(int position) {
        AdapterItem item = items.get(position);
        String className = item.getClass().getName();
        return typeIds.get(className);
    }

    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            AdapterItem item = types.get(viewType);
            Context ctx = parent.getContext();
            View view = LayoutInflater.from(ctx).inflate(item.getLayoutId(), parent, false);
            return item.getViewHolder(view, this);

        } catch (NoSuchMethodException exc) {
            Log.e(getClass().getSimpleName(), "onCreateViewHolder error: you should declare " +
                    "a constructor like this in your ViewHolder: " +
                    "public RecyclerAdapterViewHolder(View itemView, RecyclerAdapterNotifier adapter)");
            return null;

        } catch (IllegalAccessException exc) {
            Log.e(getClass().getSimpleName(), "Your ViewHolder class in " +
                    types.get(viewType).getClass().getName() + " should be public!");
            return null;

        } catch (Exception exc) {
            Log.e(getClass().getSimpleName(), "onCreateViewHolder error", exc);
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(RecyclerAdapterViewHolder holder, int position) {
        items.get(position).bind(holder);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void notifyItemChanged(RecyclerAdapterViewHolder holder, Bundle dataChanged) {
        int position = holder.getAdapterPosition();
        items.get(position).onItemChanged(dataChanged);
        notifyItemChanged(position);
    }

    /**
     * Removes all the items with a certain class from this adapter and automatically notifies changes.
     * @param clazz class of the items to be removed
     */
    public void removeAllItemsWithClass(Class<? extends AdapterItem> clazz) {
        removeAllItemsWithClass(clazz, new RemoveListener() {
            @Override
            public boolean hasToBeRemoved(AdapterItem item) {
                return true;
            }
        });
    }

    /**
     * Removes all the items with a certain class from this adapter and automatically notifies changes.
     * @param clazz class of the items to be removed
     * @param listener listener invoked for every item that is found. If the callback returns true,
     *                 the item will be removed. If it returns false, the item will not be removed
     */
    public void removeAllItemsWithClass(Class<? extends AdapterItem> clazz, RemoveListener listener) {
        if (clazz == null)
            throw new IllegalArgumentException("The class of the items can't be null!");

        if (listener == null)
            throw new IllegalArgumentException("RemoveListener can't be null!");

        if (items.isEmpty())
            return;

        boolean atLeastOneElementHasBeenRemoved = false;

        Iterator<AdapterItem> iterator = items.iterator();
        while (iterator.hasNext()) {
            AdapterItem item = iterator.next();
            if (item.getClass().getName().equals(clazz.getName()) && listener.hasToBeRemoved(item)) {
                atLeastOneElementHasBeenRemoved = true;
                iterator.remove();
            }
        }

        Integer id = typeIds.get(clazz.getName());
        if (id != null) {
            typeIds.remove(clazz.getName());
            types.remove(id);
        }

        if (atLeastOneElementHasBeenRemoved)
            notifyDataSetChanged();
    }

    /**
     * Gets the last item with a given class, together with its position.
     * @param clazz class of the item to search
     * @return Pair with position and AdapterItem or null if the adapter is empty or no items
     * exists with the given class
     */
    public Pair<Integer, AdapterItem> getLastItemWithClass(Class<? extends AdapterItem> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("The class of the items can't be null!");

        if (items.isEmpty())
            return null;

        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).getClass().getName().equals(clazz.getName())) {
                return new Pair<>(i, items.get(i));
            }
        }

        return null;
    }

    /**
     * Removes only the last item with a certain class from the adapter.
     * @param clazz class of the item to remove
     */
    public void removeLastItemWithClass(Class<? extends AdapterItem> clazz) {
        if (items.isEmpty())
            return;

        for (int i = items.size() - 1; i >= 0; i--) {
            if (items.get(i).getClass().getName().equals(clazz.getName())) {
                items.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    /**
     * Removes an item in a certain position. Does nothing if the adapter is empty or if the
     * position specified is out of adapter bounds.
     * @param position position to be removed
     */
    public void removeItemAtPosition(int position) {
        if (items.isEmpty() || position < 0 || position >= items.size())
            return;

        items.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Gets an item at a given position.
     * @param position item position
     * @return {@link AdapterItem} or null if the adapter is empty or the position is out of bounds
     */
    public AdapterItem getItemAtPosition(int position) {
        if (items.isEmpty() || position < 0 || position >= items.size())
            return null;

        return items.get(position);
    }

    /**
     * Clears all the elements in the adapter.
     */
    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }
}
