package net.gotev.recycleradapterdemo;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import net.gotev.recycleradapter.AdapterItem;
import net.gotev.recycleradapter.RecyclerAdapterNotifier;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author Aleksandar Gotev
 */

public class TextWithButtonItem extends AdapterItem<TextWithButtonItem.Holder> {

    private static final String PARAM_PRESSED = "pressed";

    private String text;
    private boolean pressed = false;

    public TextWithButtonItem(String text) {
        this.text = text;
    }

    @Override
    public boolean onFilter(String searchTerm) {
        return text.contains(searchTerm);
    }

    @Override
    public boolean onEvent(int position, Bundle data) {
        pressed = data.getBoolean(PARAM_PRESSED, false);
        return true;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_text_with_button;
    }

    @Override
    protected void bind(TextWithButtonItem.Holder holder) {
        holder.textView.setText(text);
        holder.button.setChecked(pressed);
    }

    public static class Holder extends ButterKnifeViewHolder {

        @BindView(R.id.textView)
        TextView textView;

        @BindView(R.id.toggleButton)
        ToggleButton button;

        public Holder(View itemView, RecyclerAdapterNotifier adapter) {
            super(itemView, adapter);
        }

        @OnClick(R.id.toggleButton)
        public void onToggleClick() {
            Bundle data = new Bundle();
            data.putBoolean(PARAM_PRESSED, button.isChecked());
            sendEvent(data);
        }
    }
}
