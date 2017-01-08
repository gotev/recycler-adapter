package net.gotev.recycleradapterdemo;

import android.content.Context;
import android.support.annotation.NonNull;

import net.gotev.recycleradapter.AdapterItem;

/**
 * @author Aleksandar Gotev
 */

public class SyncItem extends ExampleItem {

    private int id;

    public SyncItem(Context context, int id, String suffix) {
        super(context, "item " + id + " " + suffix);
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        SyncItem other = (SyncItem) obj;
        return id == other.id;
    }

    @Override
    public int compareTo(@NonNull AdapterItem otherItem) {
        if (otherItem.getClass() != getClass())
            return -1;

        SyncItem item = (SyncItem) otherItem;

        if (id == item.id)
            return 0;

        return id > item.id ? 1 : -1;
    }
}
