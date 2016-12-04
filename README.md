# Recycler Adapter [![Build Status](https://travis-ci.org/gotev/recycler-adapter.svg?branch=master)](https://travis-ci.org/gotev/recycler-adapter) <a href="http://www.methodscount.com/?lib=net.gotev%3Arecycleradapter%3A1.2.1"><img src="https://img.shields.io/badge/Methods and size-core: 109 | deps: 9347 | 12 KB-e91e63.svg"/></a>
Makes the use of RecyclerView easier, modular and less error-prone.

Standard `RecyclerView.Adapter` is tedious to work with, because you have to write repetitive boilerplate and spaghetti code and to concentrate all your items view logic and binding into the adapter itself, which is really bad. This library was born to be able to have the following for each element in a recycler view:

* an XML layout file, in which to define the item's view hierarchy
* a view model file (called `Item`), in which to specify the binding between the model and the view and in which to handle user interactions with the item.

In this way every item of the recycler view has its own set of files, resulting in a cleaner and easier to maintain code base.

## Setup
In your gradle dependencies add:
```groovy
compile 'net.gotev:recycleradapter:1.2.1'
```

## Basic usage tutorial
### 1. Declare the RecyclerView
In your layout resource file or where you want the `RecyclerView` (e.g. `activity_main.xml`) add the following:
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

            textView = (TextView) findViewById(R.id.textView);
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

## Reorder items with drag & drop
To be able to change the items order with drag & drop, just add this line:
```java
adapter.enableDragDrop(recyclerView);
```

## RecyclerView with different kind of items
You can have more than one kind of item in your `RecyclerView`. Just implement a different `AdapterItem` for every type you want to support, and then just add it into the adapter:
```java
adapter.add(new ExampleItem("example item"));
adapter.add(new TextWithButtonItem("text with button"));
```

Checkout the example app provided to get a real example in action.

## Empty item
It's often useful to display something on the screen when the RecyclerView is empty. To do so, simply implement a new `Item` just as you would do with a normal item in the list, then:
```java
adapter.setEmptyItem(yourEmptyItem);
```
wherever you need it in your code. It doesn't necessarily have to be invoked before
```java
recyclerView.setAdapter(adapter);
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

## Handle clicks
One of the things which you may need is to set one or more click listeners to every item. How do you do that? Let's see an example.

`item_example.xml`:
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:orientation="vertical" android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="8dp">

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/title" />

    <TextView
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textColor="@android:color/secondary_text_dark"
        android:id="@+id/subtitle" />

</LinearLayout>
```

`ExampleItem.java`:
```java
public class ExampleItem extends AdapterItem<ExampleItem.Holder> {

    private Context context;
    private String text;

    public ExampleItem(Context context, String text) {
        this.context = context;
        this.text = text;
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
        if (clickEvent != null) { // if we have a click event
            if ("title".equals(clickEvent)) { //if click comes from title
                Toast.makeText(context, "clicked TITLE at position " + position, Toast.LENGTH_SHORT).show();
            } else if ("subtitle".equals(clickEvent)) { // or from subtitle
                Toast.makeText(context, "clicked SUBTITLE at position " + position, Toast.LENGTH_SHORT).show();
            }
        }

        return false; // Item has not changed (check event lifecycle)
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
```

As you can see, to handle click events on a view, you have to create a click listener in the ViewHolder and propagate an event to the `AdapterItem`:
```java
title.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Bundle data = new Bundle();
        data.putString("click", "title");
        sendEvent(data);
    }
});
```
You can set whatever you want in the bundle to identify your events, together with additional data.

Then, to handle the click event:
```java
@Override
public boolean onEvent(int position, Bundle data) {
    if (data == null)
        return false;

    String clickEvent = data.getString("click");
    if (clickEvent != null) { // if we have a click event
        if ("title".equals(clickEvent)) { //if click comes from title
            Toast.makeText(context, "clicked TITLE at position " + position, Toast.LENGTH_SHORT).show();
        } else if ("subtitle".equals(clickEvent)) { // or from subtitle
            Toast.makeText(context, "clicked SUBTITLE at position " + position, Toast.LENGTH_SHORT).show();
        }
    }

    return false; // Item has not been changed, so return false
}
```
Look at the [event lifecycle](#eventLifecycle) to have a better comprehension.

## Handle item status and save changes into the model
It's possible to also change the model associated to an item directly from the ViewHolder. This is useful for example to notify status changes and to persist them. Imagine we need to persist a toggle button status when the user presses on it. How do we do that? Let's see an example.

`item_text_with_button.xml`:
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

    <ToggleButton
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:id="@+id/toggleButton" />
</LinearLayout>
```

`TextWithButtonItem.java`:
```java
public class TextWithButtonItem extends AdapterItem<TextWithButtonItem.Holder> {

    private static final String PARAM_PRESSED = "pressed";

    private String text;
    private boolean pressed = false;

    public TextWithButtonItem(String text) {
        this.text = text;
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
```
In the `Holder` we have added a click listener to the `ToggleButton` (in this example with ButterKnife, but you can do that also without it). When the user presses the toggle button, the `RecyclerAdapter` gets notified that an event happened in a particular position:
```java
sendEvent(data);
```
Then, `RecyclerAdapter` calls the `onEvent` method of the item which invoked `sendEvent`. In this method you can update the item's internal state. If `onEvent` method returns `true`, it means that the Item needs to be updated. `RecyclerAdapter` will call the `RecyclerView.Adapter`'s `notifyItemChanged` method and as a result, the `bind` method will be called, so your item will be updated. In this way you can safely handle the internal state of each item. If `onEvent` returns `false`, the event handling ends there and nothing more happens.

So, to recap, the <a name="eventLifecycle"></a>event lifecycle is:
```java
sendEvent(data); // send event from the ViewHolder
onEvent(int position, Bundle dataChanged); // receive event in AdapterItem
//if onEvent returns true, RecyclerAdapter invokes
//RecyclerView's notifyItemChanged method
//and the bind(Holder holder) method of the AdapterItem is called
```
