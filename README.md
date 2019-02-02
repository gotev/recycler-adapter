# Recycler Adapter [![Build Status](https://travis-ci.org/gotev/recycler-adapter.svg?branch=master)](https://travis-ci.org/gotev/recycler-adapter)

Makes the use of RecyclerView easier, modular and less error-prone.

Standard `RecyclerView.Adapter` is tedious to work with, because you have to write repetitive boilerplate and spaghetti code and to concentrate all your items view logic and binding into the adapter itself, which is really bad. This library was born to be able to have the following for each element in a recycler view:

* an XML layout file, in which to define the item's view hierarchy
* a view model file (called `Item`), in which to specify the binding between the model and the view and in which to handle user interactions with the item.

In this way every item of the recycler view has its own set of files, resulting in a cleaner and easier to maintain code base.

# Index

* [Setup](#setup)
* [Basic usage tutorial](#basicTutorial)
* [Adding different kind of items](#differentItems)
* [Empty item](#emptyItem)
* [Filter items](#filterItems)
* [Sort items](#sortItems)
* [Using ButterKnife](#butterKnife)
* [Using Kotlin Android Extensions](#kotlinAndroidExt)
* [Reorder items with drag & drop](#dragDrop)
* [Handle clicks](#handleClicks)
* [Handle item status](#handleItemStatus)
* [Event lifecycle](#eventLifecycle)
* [Leave Behind pattern](#leaveBehind)
* [Contributors](#contributors)

## <a name="setup"></a>Setup
In your gradle dependencies add:
```groovy
implementation 'net.gotev:recycleradapter:1.8.0'
```

## <a name="basicTutorial"></a>Basic usage tutorial
### 1. Declare the RecyclerView
In your layout resource file or where you want the `RecyclerView` (e.g. `activity_main.xml`) add the following:
```xml
<androidx.recyclerview.widget.RecyclerView
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
```kotlin
open class ExampleItem(private val context: Context, private val text: String)
    : AdapterItem<ExampleItem.Holder>() {

    override fun getLayoutId() = R.layout.item_example

    override fun bind(holder: ExampleItem.Holder) {
        holder.titleField.text = text
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier)
        : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val titleField: TextView by lazy { title }
    }
}
```

`LayoutContainer` is from Kotlin Android Extensions and is not mandatory, but it prevents memory leaks. [Read the article linked here](#kotlinAndroidExt)

### 4. Instantiate RecyclerView and add items
In your Activity (`onCreate` method) or Fragment (`onCreateView` method):

```kotlin
val recyclerAdapter = RecyclerAdapter()

recycler_view.apply { // recycler_view is the id of your Recycler View in the layout
    layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
    adapter = recyclerAdapter
}

//add items
recyclerAdapter.add(ExampleItem("test"))
```

## <a name="differentItems"></a>Adding different kind of items
You can have more than one kind of item in your `RecyclerView`. Just implement a different `AdapterItem` for every type you want to support, and then just add it into the adapter:

```kotlin
recyclerAdapter.add(ExampleItem("example item"))
recyclerAdapter.add(TextWithButtonItem("text with button"))
```

Checkout the example app provided to get a real example in action.

## <a name="emptyItem"></a>Empty item
It's often useful to display something on the screen when the RecyclerView is empty. To do so, simply implement a new `Item` just as you would do with a normal item in the list, then:

```kotlin
recyclerAdapter.setEmptyItem(yourEmptyItem)
```
wherever you need it in your code. It doesn't necessarily have to be invoked before

```kotlin
recyclerView.setAdapter(recyclerAdapter)
```

## <a name="filterItems"></a>Filter items
If you need to search items in your recycler view, you have to override `onFilter` method in each one of your items implementation. Let's say our `AdapterItem` has a `text` field and we want to check if the search term matches it:

```kotlin
/**
 * Gets called for every item when the [RecyclerAdapter.filter] method gets called.
 * @param searchTerm term to search for
 * @return true if the items matches the search term, false otherwise
 */
open fun onFilter(searchTerm: String): Boolean {
    return text.contains(searchTerm)
}
```

To filter the recycler view, call:

```kotlin
recyclerAdapter.filter("search item")
```
and only the items which matches the search term will be shown. To reset the search filter, pass `null` or an empty string.

## <a name="sortItems"></a>Sort items
To sort items, you have the following possible approaches.

### 1. Implement `compareTo` and call `sort` on the `RecyclerAdapter`
This is the recommended approach if you have to sort all your items by a single criteria and you have a list with only one type of `Item`. Check [compareTo JavaDoc reference](https://developer.android.com/reference/java/lang/Comparable.html#compareTo(T)) for further information. In your `AdapterItem` implement:

```kotlin
override fun compareTo(other: AdapterItem<*>): Int {
    if (other.javaClass != javaClass)
        return -1

    val item = other as SyncItem

    if (id == item.id)
        return 0

    return if (id > item.id) 1 else -1
}
```

Then call:

```kotlin
recyclerAdapter.sort(ascending = true)
```
You can see an example in action by looking at the code in the `SyncActivity` and `SyncItem` of the demo app.

### 2. Provide a custom comparator implementation
Your items doesn't necessarily have to implement `compareTo` for sorting purposes, as you can provide also the sorting implementation out of them, like this:
```java
adapter.sort(true, new Comparator<AdapterItem>() {
    @Override
    public int compare(AdapterItem itemA, AdapterItem itemB) {
        if (itemA.getClass() == RobotItem.class
            && itemB.getClass() == RobotItem.class) {
            RobotItem first = (RobotItem) itemA;
            RobotItem second = (RobotItem) itemB;
            // compare two RobotItems and return a value
        }
        return 0;
    }
});
```
The first parameter indicates if you want to sort ascending (true) or descending (false). The second parameter is a custom `Comparator` implementation. This is the recommended approach if you want to be able to sort your items by different criteria, as you can simply pass the `Comparator` implementation of the sort type you want.

### 3. Combining the two techniques
You can also combine the two techniques described above. This is the recommended approach if you have a list with different kind of items, and you want to perform different kind of grouping between items of different kind, maintaining the same sorting strategy for elements of the same type. You can implement `compareTo` in everyone of your items, to sort the items of the same kind, and a custom `Comparable` which will handle comparison between diffent kinds of items, like this:
```java
adapter.sort(true, new Comparator<AdapterItem>() {
    @Override
    public int compare(AdapterItem itemA, AdapterItem itemB) {
        // handle ordering of items of the same type with their
        // internal compareTo implementation
        if (itemA.getClass() == RobotItem.class
            && itemB.getClass() == RobotItem.class) {
            RobotItem first = (RobotItem) itemA;
            RobotItem second = (RobotItem) itemB;
            return first.compareTo(second);
        }

        if (itemA.getClass() == PersonItem.class
            && itemB.getClass() == PersonItem.class) {
            PersonItem first = (PersonItem) itemA;
            PersonItem second = (PersonItem) itemB;
            return first.compareTo(second);
        }

        // in this case, we want to put all the PersonItems
        // before the RobotItems in our list
        if (itemA.getClass() == PersonItem.class
            && itemB.getClass() == RobotItem.class) {
            return -1;
        }
        return 0;
    }
});
```

## <a name="butterKnife"></a>Using ButterKnife
You can safely use [ButterKnife](https://github.com/JakeWharton/butterknife) in your ViewHolders, however Kotlin Android Extensions are more widely used and recommended.

## <a name="kotlinAndroidExt"></a>Using Kotlin Android Extensions
If you use Kotlin in your project, you can also use Kotlin Android Extensions to bind your views in ViewHolder, but be careful to not fall in a common pitfall, explained very well here: https://proandroiddev.com/kotlin-android-extensions-using-view-binding-the-right-way-707cd0c9e648

## <a name="dragDrop"></a>Reorder items with drag & drop
To be able to change the items order with drag & drop, just add this line:

```kotlin
recyclerAdapter.enableDragDrop(recyclerView)
```

## <a name="handleClicks"></a>Handle clicks
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

## <a name="handleItemStatus"></a>Handle item status and save changes into the model
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

## <a name="leaveBehind"></a>Leave Behind pattern example implementation
In the demo app provided with the library, you can also see how to implement the [leave behind material design pattern](https://material.io/guidelines/components/lists-controls.html#lists-controls-types-of-list-controls). All the changes involved into the implementation can be seen in [this commit](https://github.com/gotev/recycler-adapter/commit/fa240519025f98ba609395034f42e89d5bb777fd). This implementation has not been included into the base library deliberately, to avoid depending on external libraries just for a single kind of item implementation. You can easily import the needed code in your project from the demo app sources if you want to have leave behind implementation.

## <a name="contributors"></a>Contributors
Thanks to:
* [Kristiyan Petrov](https://github.com/kristiyanP) for the beta testing and code review
* [Nicola Gallazzi](https://github.com/ngallazzi) for helping transitioning the library to AndroidX
