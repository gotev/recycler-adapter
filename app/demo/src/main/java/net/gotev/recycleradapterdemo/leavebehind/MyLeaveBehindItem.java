package net.gotev.recycleradapterdemo.leavebehind;

import android.view.View;
import android.widget.TextView;

import net.gotev.recycleradapter.RecyclerAdapterNotifier;
import net.gotev.recycleradapterdemo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Aleksandar Gotev
 */

public class MyLeaveBehindItem extends LeaveBehindAdapterItem<MyLeaveBehindItem.Holder> {

    private String value;
    private String background;

    public MyLeaveBehindItem(String value, String background) {
        this.value = value;
        this.background = background;
    }

    @Override
    protected void bind(Holder holder) {
        holder.name.setText(value);
        holder.delete.setText(background);
    }

    public static class Holder extends LeaveBehindViewHolder {

        @BindView(R.id.name)
        TextView name;

        @BindView(R.id.delete)
        TextView delete;

        public Holder(final View itemView, final RecyclerAdapterNotifier adapter) {
            super(itemView, adapter);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public int getContentViewId() {
            return R.layout.swipe_foregound_layout;
        }

        @Override
        public int getLeaveBehindId() {
            return R.layout.swipe_background_layout;
        }
    }
}
