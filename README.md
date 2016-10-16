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
Create your item layout. For example:
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

### 3. Instantiate RecyclerView and add items
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
