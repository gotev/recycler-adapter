## Basic usage
### 1. Declare RecyclerView
In your layout resource file (e.g. `activity_main.xml`) place the following:
```xml
<android.support.v7.widget.RecyclerView
    android:id="@+id/recycler_view"
    android:scrollbars="vertical"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

### 2. Create your item layout
Create your item layout (e.g. `item_example.xml`). For example:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/textView" />
</LinearLayout>
```

### 3. Create the item
```java
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

    public static class Holder extends RecyclerAdapterViewHolder {

        TextView textView;

        public Holder(View itemView, RecyclerAdapterNotifier adapter) {
            super(itemView, adapter);

            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
```

### 4. Instantiate RecyclerView and add items
In your Activity (`onCreate` method) or Fragment (`onCreateView` method):
```java
RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
RecyclerAdapter adapter = new RecyclerAdapter();
recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
recyclerView.setAdapter(adapter);

//add items
adapter.add(new ExampleItem("test"));
```

## Using ButterKnife
You can safely use [ButterKnife](https://github.com/JakeWharton/butterknife) in your ViewHolders. Example:
```java
public static class Holder extends RecyclerAdapterViewHolder {

    @BindView(R.id.textView)
    TextView textView;

    public Holder(View itemView, RecyclerAdapterNotifier adapter) {
        super(itemView, adapter);
        ButterKnife.bind(this, itemView);
    }
}
```

If you are using [ButterKnife](https://github.com/JakeWharton/butterknife) in your project and you want to minimize boilerplate code in your ViewHolders, you can extend `RecyclerAdapterViewHolder`, implement ButterKnife in it, and then extend all of your ViewHolders from it:
```java
public abstract class ButterKnifeViewHolder extends RecyclerAdapterViewHolder {
    public ButterKnifeViewHolder(View itemView, RecyclerAdapterNotifier adapter) {
        super(itemView, adapter);
        ButterKnife.bind(this, itemView);
    }
}
```
Then you can use it like this:
```java
public static class Holder extends ButterKnifeViewHolder {

    @BindView(R.id.textView)
    TextView textView;

    public Holder(View itemView, RecyclerAdapterNotifier adapter) {
        super(itemView, adapter);
    }
}
```

## Handle item status and save changes into the model
It's possible to also change the model associated to an item directly from the ViewHolder. This is useful for example to notify status changes and to persist them. Imagine we need to persist a toggle button status when the user presses on it. How do we do that? Let's see an example:
```java
public class TextWithButtonItem extends AdapterItem<TextWithButtonItem.Holder> {

    private static final String PARAM_PRESSED = "pressed";

    private String text;
    private boolean pressed = false;

    public TextWithButtonItem(String text) {
        this.text = text;
    }

    @Override
    public void onItemChanged(Bundle dataChanged) {
        pressed = dataChanged.getBoolean(PARAM_PRESSED, false);
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
            getAdapter().notifyItemChanged(this, data);
        }
    }
}
```
In the `Holder` we have added a click listener to the `ToggleButton` (in this example with ButterKnife, but you can do that also without it). When the user presses the toggle button, the `RecyclerAdapter` gets notified that something has changed in a particular position:
```java
getAdapter().notifyItemChanged(this, data);
```
Then, the adapter calls the `onItemChanged` method of the item corresponding to the position of the ViewHolder, and the item has a chance to update its internal data or to do something else. After that, the `RecyclerAdapter` updates the corresponding item by calling the `bind` method. In this way you can safely handle the internal state of each item.
