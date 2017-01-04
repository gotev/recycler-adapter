package net.gotev.recycleradapterdemo.leavebehind;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import net.gotev.recycleradapter.RecyclerAdapterNotifier;
import net.gotev.recycleradapter.RecyclerAdapterViewHolder;
import net.gotev.recycleradapterdemo.R;

/**
 * Base RecyclerAdapterViewHolder to extend when implementing leave-behind material pattern.
 * @author Aleksandar Gotev
 */

public abstract class LeaveBehindViewHolder extends RecyclerAdapterViewHolder {

    FrameLayout contentView;
    FrameLayout leaveBehindView;

    public LeaveBehindViewHolder(final View itemView, final RecyclerAdapterNotifier adapter) {
        super(itemView, adapter);

        Context context = itemView.getContext();

        contentView = (FrameLayout) findViewById(R.id.swipe_content_view);
        leaveBehindView = (FrameLayout) findViewById(R.id.swipe_background_layout);

        LayoutInflater.from(context).inflate(getContentViewId(), contentView);
        LayoutInflater.from(context).inflate(getLeaveBehindId(), leaveBehindView);
    }

    public abstract int getContentViewId();

    public abstract int getLeaveBehindId();

}
