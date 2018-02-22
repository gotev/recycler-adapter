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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;

import static android.support.v7.widget.helper.ItemTouchHelper.DOWN;
import static android.support.v7.widget.helper.ItemTouchHelper.END;
import static android.support.v7.widget.helper.ItemTouchHelper.START;
import static android.support.v7.widget.helper.ItemTouchHelper.UP;

/**
 * Helper class to easily work with Android's RecyclerView.Adapter.
 *
 * @author Aleksandar Gotev
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapterViewHolder>
        implements RecyclerAdapterNotifier {

    private LinkedHashMap<String, Integer> typeIds;
    private LinkedHashMap<Integer, AdapterItem> types;
    private List<AdapterItem> itemsList;
    private AdapterItem emptyItem;
    private int emptyItemId;

    private List<AdapterItem> filtered;
    private boolean showFiltered;

    /**
     * Applies swipe gesture detection on a RecyclerView items.
     *
     * @param recyclerView recycler view o which to apply the swipe gesture
     * @param listener     listener called when a swipe is performed on one of the items
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
        itemsList = new ArrayList<>();
        emptyItem = null;
    }

    private List<AdapterItem> getItems() {
        return showFiltered ? filtered : itemsList;
    }

    /**
     * Sets the item to show when the recycler adapter is empty.
     *
     * @param item item to show when the recycler adapter is empty
     */
    public void setEmptyItem(AdapterItem item) {
        emptyItem = item;
        emptyItemId = ViewIdGenerator.generateViewId();

        if (getItems().isEmpty())
            notifyItemInserted(0);
    }

    /**
     * Adds a new item to this adapter
     *
     * @param item item to add
     * @return {@link RecyclerAdapter}
     */
    public RecyclerAdapter add(AdapterItem item) {
        registerItemType(item);
        getItems().add(item);
        removeEmptyItemIfItHasBeenConfigured();

        notifyItemInserted(getItems().size() - 1);
        return this;
    }

    /**
     * Gets the position of an item in an adapter.
     * <p>
     * For the method to work properly, all the items has to override the
     * {@link AdapterItem#equals(Object)} and {@link AdapterItem#hashCode()} methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item (plus some other changes). Check the example in {@link RecyclerAdapter#add(AdapterItem)}
     *
     * @param item item object
     * @return the item's position or -1 if the item does not exist
     */
    public int getItemPosition(AdapterItem item) {
        return getItems().indexOf(item);
    }

    /**
     * Adds an item into the adapter or updates it if already existing.
     * <p>
     * For the update to work properly, all the items has to override the
     * {@link AdapterItem#equals(Object)} and {@link AdapterItem#hashCode()} methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item (plus some other changes).
     * <p>
     * As an example consider the following item
     * (written in pseudocode, to write less code):
     * <pre>
     * Person extends AdapterItem {
     *      String id;
     *      String name;
     *      String city;
     * }
     * </pre>
     * in this case every person is uniquely identified by its id, while other data may change, so
     * the {@link AdapterItem#equals(Object)} method will look like this:
     * <p>
     * <pre>
     * public boolean equals(Object obj) {
     *     if (this == obj) {
     *         return true;
     *     }
     *
     *     if (obj == null || getClass() != obj.getClass()) {
     *         return false;
     *     }
     *
     *     Person other = (Person) obj;
     *     return other.getId().equals(id);
     * }
     * </pre>
     * <p>
     * If the item already exists in the list, by impementing
     * {@link AdapterItem#hasToBeReplacedBy(AdapterItem)} in your AdapterItem, you can decide
     * when the new item should replace the existing one in the list, reducing the workload of
     * the recycler view.
     * <p>
     * Check hasToBeReplacedBy method JavaDoc for more information.
     *
     * @param item item to add or update
     * @return {@link RecyclerAdapter}
     */
    public RecyclerAdapter addOrUpdate(AdapterItem item) {
        int itemIndex = getItemPosition(item);

        if (itemIndex < 0) {
            return add(item);
        }

        AdapterItem internalItem = getItems().get(itemIndex);
        if (internalItem.hasToBeReplacedBy(item)) { // the item needs to be updated
            updateItemAtPosition(item, itemIndex);
        }

        return this;
    }

    private void updateItemAtPosition(AdapterItem item, int position) {
        getItems().set(position, item);
        notifyItemChanged(position);
    }

    /**
     * Syncs the internal list of items with a list passed as parameter.
     * Adds, updates or deletes internal items, with RecyclerView animations.
     * <p>
     * For the sync to work properly, all the items has to override the
     * {@link AdapterItem#equals(Object)} and {@link AdapterItem#hashCode()} methods and
     * implement the required business logic code to detect if two instances are referring to the
     * same item. Check the example in {@link RecyclerAdapter#add(AdapterItem)}.
     * If two instances are referring to the same item, you can decide if the item should be
     * replaced by the new one, by implementing {@link AdapterItem#hasToBeReplacedBy(AdapterItem)}.
     * Check hasToBeReplacedBy method JavaDoc for more information.
     *
     * @param newItems list of new items. Passing a null or empty list will result in
     *                 {@link RecyclerAdapter#clear()} method call.
     * @return {@link RecyclerAdapter}
     */
    public RecyclerAdapter syncWithItems(final List<? extends AdapterItem> newItems) {
        if (newItems == null || newItems.isEmpty()) {
            clear();
            return this;
        }

        ListIterator<AdapterItem> iterator = getItems().listIterator();

        while (iterator.hasNext()) {
            int internalListIndex = iterator.nextIndex();
            AdapterItem item = iterator.next();

            int indexInNewItemsList = newItems.indexOf(item);
            // if the item does not exist in the new list, it means it has been deleted
            if (indexInNewItemsList < 0) {
                iterator.remove();
                notifyItemRemoved(internalListIndex);
            } else { // the item exists in the new list
                AdapterItem newItem = newItems.get(indexInNewItemsList);
                if (item.hasToBeReplacedBy(newItem)) { // the item needs to be updated
                    updateItemAtPosition(newItem, internalListIndex);
                }
                newItems.remove(indexInNewItemsList);
            }
        }

        for (AdapterItem newItem : newItems) {
            add(newItem);
        }

        return this;
    }

    /**
     * Removes an item from the adapter.
     * <p>
     * For the remove to work properly, all the items has to override the
     * {@link AdapterItem#equals(Object)} and {@link AdapterItem#hashCode()} methods.
     * Check the example in {@link RecyclerAdapter#addOrUpdate(AdapterItem)}
     *
     * @param item item to remove
     * @return true if the item has been correctly removed or false if the item does not exist
     */
    public boolean removeItem(AdapterItem item) {
        int itemIndex = getItems().indexOf(item);

        if (itemIndex < 0) {
            return false;
        }

        return removeItemAtPosition(itemIndex);
    }

    /**
     * Adds a new item to this adapter
     *
     * @param item     item to add
     * @param position position at which to add the element. The item previously at
     *                 (position) will be at (position + 1) and the same for all the subsequent
     *                 elements
     * @return {@link RecyclerAdapter}
     */
    public RecyclerAdapter addAtPosition(AdapterItem item, int position) {
        registerItemType(item);
        getItems().add(position, item);
        removeEmptyItemIfItHasBeenConfigured();

        notifyItemInserted(position);
        return this;
    }

    private void registerItemType(AdapterItem item) {
        final String className = item.getClass().getName();

        if (!typeIds.containsKey(className)) {
            int viewId = ViewIdGenerator.generateViewId();
            typeIds.put(className, viewId);
            types.put(viewId, item);
        }
    }

    private void removeEmptyItemIfItHasBeenConfigured() {
        // this is necessary to prevent IndexOutOfBoundsException on RecyclerView when the
        // first item gets added and an empty item has been configured
        if (getItems().size() == 1 && emptyItem != null) {
            notifyItemRemoved(0);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (adapterIsEmptyAndEmptyItemIsDefined()) {
            return emptyItemId;
        }

        AdapterItem item = getItems().get(position);
        String className = item.getClass().getName();
        return typeIds.get(className);
    }

    @Override
    public RecyclerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        try {
            AdapterItem item;

            if (adapterIsEmptyAndEmptyItemIsDefined() && viewType == emptyItemId) {
                item = emptyItem;
            } else {
                item = types.get(viewType);
            }

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
        if (adapterIsEmptyAndEmptyItemIsDefined()) {
            emptyItem.bind(holder);
        } else {
            getItems().get(position).bind(holder);
        }
    }

    /**
     * Gives all the items in the adapter
     *
     * @return the list of items in the adapter
     */
    public List<AdapterItem> getItemsList() {
        return itemsList;
    }

    @Override
    public int getItemCount() {
        if (adapterIsEmptyAndEmptyItemIsDefined())
            return 1;

        return getItems().size();
    }

    @Override
    public void sendEvent(RecyclerAdapterViewHolder holder, Bundle data) {
        int position = holder.getAdapterPosition();

        if (position < 0 || position >= getItems().size())
            return;

        if (getItems().get(position).onEvent(position, data))
            notifyItemChanged(position);
    }

    /**
     * Removes all the items with a certain class from this adapter and automatically notifies changes.
     *
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
     *
     * @param clazz    class of the items to be removed
     * @param listener listener invoked for every item that is found. If the callback returns true,
     *                 the item will be removed. If it returns false, the item will not be removed
     */
    public void removeAllItemsWithClass(Class<? extends AdapterItem> clazz, RemoveListener listener) {
        if (clazz == null)
            throw new IllegalArgumentException("The class of the items can't be null!");

        if (listener == null)
            throw new IllegalArgumentException("RemoveListener can't be null!");

        if (getItems().isEmpty())
            return;

        ListIterator<AdapterItem> iterator = getItems().listIterator();
        int index;
        while (iterator.hasNext()) {
            index = iterator.nextIndex();
            AdapterItem item = iterator.next();
            if (item.getClass().getName().equals(clazz.getName()) && listener.hasToBeRemoved(item)) {
                iterator.remove();
                notifyItemRemoved(index);
            }
        }

        Integer id = typeIds.get(clazz.getName());
        if (id != null) {
            typeIds.remove(clazz.getName());
            types.remove(id);
        }
    }

    /**
     * Gets the last item with a given class, together with its position.
     *
     * @param clazz class of the item to search
     * @return Pair with position and AdapterItem or null if the adapter is empty or no items
     * exists with the given class
     */
    public Pair<Integer, AdapterItem> getLastItemWithClass(Class<? extends AdapterItem> clazz) {
        if (clazz == null)
            throw new IllegalArgumentException("The class of the items can't be null!");

        if (getItems().isEmpty())
            return null;

        for (int i = getItems().size() - 1; i >= 0; i--) {
            if (getItems().get(i).getClass().getName().equals(clazz.getName())) {
                return new Pair<>(i, getItems().get(i));
            }
        }

        return null;
    }

    /**
     * Removes only the last item with a certain class from the adapter.
     *
     * @param clazz class of the item to remove
     */
    public void removeLastItemWithClass(Class<? extends AdapterItem> clazz) {
        if (getItems().isEmpty())
            return;

        for (int i = getItems().size() - 1; i >= 0; i--) {
            if (getItems().get(i).getClass().getName().equals(clazz.getName())) {
                getItems().remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    /**
     * Removes an item in a certain position. Does nothing if the adapter is empty or if the
     * position specified is out of adapter bounds.
     *
     * @param position position to be removed
     * @return true if the item has been removed, false if it doesn't exist or the position
     * is out of bounds
     */
    public boolean removeItemAtPosition(int position) {
        if (getItems().isEmpty() || position < 0 || position >= getItems().size())
            return false;

        getItems().remove(position);
        notifyItemRemoved(position);

        return true;
    }

    /**
     * Gets an item at a given position.
     *
     * @param position item position
     * @return {@link AdapterItem} or null if the adapter is empty or the position is out of bounds
     */
    public AdapterItem getItemAtPosition(int position) {
        if (getItems().isEmpty() || position < 0 || position >= getItems().size())
            return null;

        return getItems().get(position);
    }

    /**
     * Clears all the elements in the adapter.
     */
    public void clear() {
        int itemsSize = getItems().size();
        getItems().clear();
        if (itemsSize > 0) {
            notifyItemRangeRemoved(0, itemsSize);
        }
    }

    private boolean adapterIsEmptyAndEmptyItemIsDefined() {
        return getItems().isEmpty() && emptyItem != null;
    }

    /**
     * Enables reordering of the list through drag and drop, which is activated when the user
     * long presses on an item.
     *
     * @param recyclerView recycler view on which to apply the drag and drop
     */
    public void enableDragDrop(RecyclerView recyclerView) {
        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, DOWN | UP | START | END);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int sourcePosition = viewHolder.getAdapterPosition();
                int targetPosition = target.getAdapterPosition();

                Collections.swap(getItems(), sourcePosition, targetPosition);
                notifyItemMoved(sourcePosition, targetPosition);

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //Do nothing here
            }
        });

        touchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Filters this adapter with a given search term and shows only the items which
     * matches it.
     * <p>
     * For the filter to work properly, each item must override the
     * {@link AdapterItem#onFilter(String)} method and provide custom implementation.
     *
     * @param searchTerm search term
     */
    public void filter(final String searchTerm) {
        if (itemsList == null || itemsList.isEmpty()) {
            return;
        }

        if (searchTerm == null || searchTerm.isEmpty()) {
            showFiltered = false;
            notifyDataSetChanged();
            return;
        }

        if (filtered == null) {
            filtered = new ArrayList<>();
        } else {
            filtered.clear();
        }

        for (AdapterItem item : itemsList) {
            if (item.onFilter(searchTerm)) {
                filtered.add(item);
            }
        }

        showFiltered = true;
        notifyDataSetChanged();

    }

    /**
     * Sort items.
     * <p>
     * For this method to work properly, each item must override the
     * {@link AdapterItem#compareTo(AdapterItem)} method.
     *
     * @param ascending true for ascending order (A-Z) or false for descending order (Z-A)
     */
    public void sort(boolean ascending) {
        List<AdapterItem> items = getItems();

        if (items == null || items.isEmpty())
            return;

        if (ascending) {
            Collections.sort(items);
        } else {
            Collections.sort(items, Collections.<AdapterItem>reverseOrder());
        }

        notifyDataSetChanged();
    }

    /**
     * Sort items.
     * <p>
     * With this method, the items doesn't have to override the
     * {@link AdapterItem#compareTo(AdapterItem)} method, as the comparator is passed as
     * argument and is responsible of item comparison. You can use this sort method if your items
     * has to be sorted with many different strategies and not just one
     * (e.g. order items by name, by date, ...).
     *
     * @param ascending  true for ascending order (A-Z) or false for descending order (Z-A).
     *                   Ascending order follows the passed comparator sorting algorithm order,
     *                   descending order uses the inverse order
     * @param comparator custom comparator implementation
     */
    public void sort(boolean ascending, Comparator<AdapterItem> comparator) {
        List<AdapterItem> items = getItems();

        if (items == null || items.isEmpty())
            return;

        if (ascending) {
            Collections.sort(getItems(), comparator);
        } else {
            Collections.sort(getItems(), Collections.reverseOrder(comparator));
        }

        notifyDataSetChanged();
    }
}
