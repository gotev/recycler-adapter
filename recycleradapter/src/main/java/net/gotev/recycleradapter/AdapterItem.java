package net.gotev.recycleradapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * Abstract class to extend to create ViewHolders.
 * @author Aleksandar Gotev
 * @param <T> ViewHolder subclass
 */
public abstract class AdapterItem<T extends RecyclerAdapterViewHolder> implements Comparable<AdapterItem> {

    /**
     * Gets called by {@link RecyclerAdapter#sendEvent(RecyclerAdapterViewHolder, Bundle)}
     * @param position position at which the event happened
     * @param data additional click data
     * @return true if the view must be rebinded after this method returns (e.g. you updated some
     *         data in the model and you want to display it), otherwise false (e.g. if you are
     *         simply handling a click which does not involve data changes)
     */
    public boolean onEvent(int position, Bundle data) {
        return false;
    }

    /**
     * Gets called for every item when the {@link RecyclerAdapter#filter(String)} method gets called.
     * @param searchTerm term to search for
     * @return true if the items matches the search term, false otherwise
     */
    public boolean onFilter(final String searchTerm) {
        return true;
    }

    /**
     * Gets called when you perform {@link RecyclerAdapter#syncWithItems(List)}, specifically when
     * an item in the new list equals to this one (according to {@link AdapterItem#equals(Object)}
     * implementation). In this case, the item has to decide whether or not it should be replaced
     * by the new one. Generally this is useful when for example you have a person identified
     * uniquely by ID (equals returns true if two items have the same ID), but you want to update
     * the item only if the rest of the data has been changed.
     * If you return false, the item will remain unchanged. If you return true, the item will be
     * replaced by the new one, and RecyclerAdapter's notifyItemChanged method will be
     * called to update the binding.
     * @param newItem item in the new list whose {@link AdapterItem#equals(Object)} returns the
     *                same value as this item
     * @return true to replace this item with the new item, false otherwise
     */
    public boolean hasToBeReplacedBy(AdapterItem newItem) {
        return true;
    }

    /**
     * Creates a new ViewHolder instance, by inferring the ViewHolder type from the generic passed
     * to this class
     * @param view View to be passed to the ViewHolder
     * @param adapter {@link RecyclerAdapter} instance
     * @return ViewHolder
     * @throws NoSuchMethodException if no mathing constructor are found in the ViewHolder subclass
     * @throws InstantiationException if an error happens during instantiation of the ViewHolder subclass
     * @throws InvocationTargetException if an error happens during a method invocation of the ViewHolder subclass
     * @throws IllegalAccessException if a method, field or class has been declared with insufficient access control modifiers
     */
    @SuppressWarnings("unchecked")
    T getViewHolder(View view, RecyclerAdapterNotifier adapter)
            throws NoSuchMethodException, InstantiationException,
            InvocationTargetException, IllegalAccessException {

        // analyze all the public classes and interfaces that are members of the class represented
        // by this Class object and search for the first RecyclerAdapterViewHolder
        // implementation. This should also work if RecyclerAdapterViewHolder subclass
        // hierarchy is present, as the first one should be the last of the subclasses
        for (Class cl : getClass().getClasses()) {
            if (RecyclerAdapterViewHolder.class.isAssignableFrom(cl)) {
                Class<T> clazz = (Class<T>) cl;
                return clazz.getConstructor(View.class, RecyclerAdapterNotifier.class)
                        .newInstance(view, adapter);
            }
        }

        Log.e(getClass().getSimpleName(), "No ViewHolder implementation found! " +
                "Please check that all your ViewHolder implementations are: 'public static' and " +
                "not private or protected, otherwise reflection will not work!");
        return null;

    }

    /**
     * Returns the layout ID for this item
     * @return layout ID
     */
    public abstract int getLayoutId();

    /**
     * Bind the current item with the view
     * @param holder ViewHolder on which to bind data
     */
    protected abstract void bind(T holder);

    @Override
    public int compareTo(@NonNull AdapterItem otherItem) {
        return 0;
    }
}
