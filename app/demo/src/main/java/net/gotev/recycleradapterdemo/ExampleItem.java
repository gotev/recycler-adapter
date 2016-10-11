package net.gotev.recycleradapterdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import net.gotev.recycleradapter.AdapterItem;
import net.gotev.recycleradapter.RecyclerAdapterNotifier;

import butterknife.BindView;

/**
 * @author Aleksandar Gotev
 */

public class ExampleItem extends AdapterItem<ExampleItem.Holder> {

    private String text;

    public ExampleItem(String text) {
        this.text = text;
    }

    @Override
    public void onItemChanged(Bundle dataChanged) {

    }

    @Override
    public int getLayoutId() {
        return R.layout.item_example;
    }

    @Override
    protected void bind(ExampleItem.Holder holder) {
        holder.textView.setText(text);
    }

    public static class Holder extends ButterKnifeViewHolder {

        @BindView(R.id.textView)
        TextView textView;

        public Holder(View itemView, RecyclerAdapterNotifier adapter) {
            super(itemView, adapter);
        }
    }
}
