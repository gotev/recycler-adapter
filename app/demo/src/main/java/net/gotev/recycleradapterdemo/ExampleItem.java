package net.gotev.recycleradapterdemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.gotev.recycleradapter.AdapterItem;
import net.gotev.recycleradapter.RecyclerAdapterNotifier;
import net.gotev.recycleradapter.RecyclerAdapterViewHolder;

/**
 * @author Aleksandar Gotev
 */

public class ExampleItem extends AdapterItem<ExampleItem.Holder> {

    private Context context;
    private String text;

    public ExampleItem(Context context, String text) {
        this.context = context;
        this.text = text;
    }

    @Override
    public boolean onFilter(String searchTerm) {
        return text.contains(searchTerm);
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_example;
    }

    @Override
    public boolean onEvent(int position, Bundle data) {
        if (data == null)
            return false;

        String clickEvent = data.getString("click");
        if (clickEvent != null) {
            if ("title".equals(clickEvent)) {
                Toast.makeText(context, "clicked TITLE at position " + position, Toast.LENGTH_SHORT).show();
            } else if ("subtitle".equals(clickEvent)) {
                Toast.makeText(context, "clicked SUBTITLE at position " + position, Toast.LENGTH_SHORT).show();
            }
        }

        return false;
    }

    @Override
    protected void bind(ExampleItem.Holder holder) {
        holder.title.setText(text);
        holder.subtitle.setText("subtitle");
    }

    public static class Holder extends RecyclerAdapterViewHolder {

        TextView title;
        TextView subtitle;

        public Holder(View itemView, RecyclerAdapterNotifier adapter) {
            super(itemView, adapter);

            title = (TextView) findViewById(R.id.title);

            title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle data = new Bundle();
                    data.putString("click", "title");
                    sendEvent(data);
                }
            });

            subtitle = (TextView) findViewById(R.id.subtitle);

            subtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle data = new Bundle();
                    data.putString("click", "subtitle");
                    sendEvent(data);
                }
            });
        }
    }
}
