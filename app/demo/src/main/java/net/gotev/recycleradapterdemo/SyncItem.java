package net.gotev.recycleradapterdemo;

import android.content.Context;

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
}
