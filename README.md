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
* [Filter items (to implement searchBar)](#filterItems)
* [Sort items](#sortItems)
* [Using ButterKnife](#butterKnife)
* [Using Kotlin Android Extensions](#kotlinAndroidExt)
* [Reorder items with drag & drop](#dragDrop)
* [Handle clicks](#handleClicks)
* [Handle item status](#handleItemStatus)
* [Event lifecycle](#eventLifecycle)
* [Single and Multiple selection of items](#itemsSelection)
    * [Getting selected items in a selection group](#getting-selected-items-in-a-selection-group)
    * [Programmatically select items](#programmatically-select-items)
    * [Replacing selection groups items and Master/Slave selection groups](#replacing-selection-groups-items-and-masterslave-selection-groups)
* [Leave Behind pattern](#leaveBehind)
* [Contributors](#contributors)

## <a name="setup"></a>Setup
In your gradle dependencies add:
```groovy
implementation 'net.gotev:recycleradapter:2.1.0'
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
Your items doesn't necessarily have to implement `compareTo` for sorting purposes, as you can provide also the sorting implementation outside of them, like this:
```kotlin
recyclerAdapter.sort(ascending = true, comparator = object : Comparator<AdapterItem<*>> {
    override fun compare(itemA: AdapterItem<*>, itemB: AdapterItem<*>): Int {
        if (itemA.javaClass == RobotItem::class.java && itemB.javaClass == RobotItem::class.java) {
            val first = itemA as RobotItem
            val second = itemB as RobotItem
            // compare two RobotItems and return a value
        }
        return 0
    }
})
```
The first parameter indicates if you want to sort ascending (true) or descending (false). The second parameter is a custom `Comparator` implementation. This is the recommended approach if you want to be able to sort your items by different criteria, as you can simply pass the `Comparator` implementation of the sort type you want.

### 3. Combining the two techniques
You can also combine the two techniques described above. This is the recommended approach if you have a list with different kind of items, and you want to perform different kind of grouping between items of different kind, maintaining the same sorting strategy for elements of the same type. You can implement `compareTo` in everyone of your items, to sort the items of the same kind, and a custom `Comparable` which will handle comparison between diffent kinds of items, like this:
```kotlin
recyclerAdapter.sort(ascending = true, comparator = object : Comparator<AdapterItem<*>> {
    override fun compare(itemA: AdapterItem<*>, itemB: AdapterItem<*>): Int {
        // handle ordering of items of the same type with their
        // internal compareTo implementation
        if (itemA.javaClass == RobotItem::class.java && itemB.javaClass == RobotItem::class.java) {
            val first = itemA as RobotItem
            val second = itemB as RobotItem
            return first.compareTo(second)
        }

        if (itemA.javaClass == PersonItem::class.java && itemB.javaClass == PersonItem::class.java) {
            val first = itemA as PersonItem
            val second = itemB as PersonItem
            return first.compareTo(second)
        }

        // in this case, we want to put all the PersonItems
        // before the RobotItems in our list
        return if (itemA.javaClass == PersonItem::class.java && itemB.javaClass == RobotItem::class.java) {
            -1
        } else 0
    }
})
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

`ExampleItem.kt`:
```kotlin
open class ExampleItem(private val context: Context, private val text: String)
    : AdapterItem<ExampleItem.Holder>() {

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun getLayoutId() = R.layout.item_example

    override fun onEvent(position: Int, data: Bundle?): Boolean {
        if (data == null)
            return false

        val clickEvent = data.getString("click") ?: return false

        if ("title" == clickEvent) {
            Toast.makeText(context, "clicked TITLE at position $position", Toast.LENGTH_SHORT).show()
        } else if ("subtitle" == clickEvent) {
            Toast.makeText(context, "clicked SUBTITLE at position $position", Toast.LENGTH_SHORT).show()
        }

        return false
    }

    override fun bind(holder: Holder) {
        holder.titleField.text = text
        holder.subtitleField.text = "subtitle"
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier)
        : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val titleField: TextView by lazy { title }
        internal val subtitleField: TextView by lazy { subtitle }

        init {
            titleField.setOnClickListener {
                val data = Bundle()
                data.putString("click", "title")
                sendEvent(data)
            }

            subtitleField.setOnClickListener {
                val data = Bundle()
                data.putString("click", "subtitle")
                sendEvent(data)
            }
        }
    }
}
```

As you can see, to handle click events on a view, you have to create a click listener in the ViewHolder and propagate an event to the `AdapterItem`:
```kotlin
titleField.setOnClickListener {
    val data = Bundle()
    data.putString("click", "title")
    sendEvent(data)
}
```
You can set whatever you want in the bundle to identify your events, together with additional data.

Then, to handle the click event:
```kotlin
override fun onEvent(position: Int, data: Bundle?): Boolean {
    if (data == null)
        return false

    val clickEvent = data.getString("click") ?: return false

    if ("title" == clickEvent) {
        Toast.makeText(context, "clicked TITLE at position $position", Toast.LENGTH_SHORT).show()
    } else if ("subtitle" == clickEvent) {
        Toast.makeText(context, "clicked SUBTITLE at position $position", Toast.LENGTH_SHORT).show()
    }

    return false
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

`TextWithButtonItem.kt`:
```kotlin
class TextWithButtonItem(private val text: String) : AdapterItem<TextWithButtonItem.Holder>() {

    companion object {
        private const val PARAM_PRESSED = "pressed"
    }

    private var pressed = false

    override fun onFilter(searchTerm: String) = text.contains(searchTerm)

    override fun onEvent(position: Int, data: Bundle?): Boolean {
        pressed = data?.getBoolean(PARAM_PRESSED, false) ?: false
        return true
    }

    override fun getLayoutId() = R.layout.item_text_with_button

    override fun bind(holder: Holder) {
        holder.textViewField.text = text
        holder.buttonField.isChecked = pressed
    }

    class Holder(itemView: View, adapter: RecyclerAdapterNotifier) : RecyclerAdapterViewHolder(itemView, adapter), LayoutContainer {

        override val containerView: View?
            get() = itemView

        internal val textViewField: TextView by lazy { textView }
        internal val buttonField: ToggleButton by lazy { toggleButton }

        init {
            buttonField.setOnClickListener {
                val data = Bundle()
                data.putBoolean(PARAM_PRESSED, buttonField.isChecked)
                sendEvent(data)
            }
        }
    }
}
```
In the `Holder` we have added a click listener to the `ToggleButton` (in this example with ButterKnife, but you can do that also without it). When the user presses the toggle button, the `RecyclerAdapter` gets notified that an event happened in a particular position:
```kotlin
sendEvent(data)
```
Then, `RecyclerAdapter` calls the `onEvent` method of the item which invoked `sendEvent`. In this method you can update the item's internal state. If `onEvent` method returns `true`, it means that the Item needs to be updated. `RecyclerAdapter` will call the `RecyclerView.Adapter`'s `notifyItemChanged` method and as a result, the `bind` method will be called, so your item will be updated. In this way you can safely handle the internal state of each item. If `onEvent` returns `false`, the event handling ends there and nothing more happens.

So, to recap, the <a name="eventLifecycle"></a>event lifecycle is:
```kotlin
sendEvent(data) // send event from the ViewHolder
onEvent(position: Int, data: Bundle?) // receive event in AdapterItem
//if onEvent returns true, RecyclerAdapter invokes
//RecyclerView's notifyItemChanged method
//and the bind(Holder holder) method of the AdapterItem is called
```

## <a name="itemsSelection"></a>Single and Multiple selection of items
Often RecyclerViews are used to implement settings toggles, bottom sheets and other UI to perform selections. What is needed in the vast majority of the cases is:

* a way to select a single item from a list of items
* a way to select many items from a list of items

To complicate things, many times a single RecyclerView has to contain various groups of selectable items, for example let's imagine an online order form in which the user has to select:

* a payment method from a list of supported ones (only one selection)
* additions like extra support, gift packaging, accessories, ... (many selections)
* shipping address (only one selection)
* billing address (only one selection)

So it gets pretty complicated, huh 😨? Don't worry, `RecyclerAdapter` to the rescue! 🙌🏼

### Selectable AdapterItem implementation
From release `2.0.0` onwards, support for `selection groups` has been added.

1. First of all, override `getSelectionGroup` in your `AdapterItem`:
```kotlin
/**
 * Returns the ID of this item's selection group. By default it's null.
 * This is used when you want to perform single or multiple selections in the RecyclerView and
 * you need to know all the items belonging to that group.
 * For an item to be selectable, it's necessary that it belongs to a selection group.
 *
 * By returning null, the item does not belong to any selection group.
 */
open fun getSelectionGroup(): String? = null
```
For example:
```kotlin
override fun getSelectionGroup() = "MySelectionGroup"
```

2. Then if you need to do some stuff when selection changes, override `onSelectionChanged`:
```kotlin
/**
 * Method called only when using single or multiple selection and the selection status of this
 * item has changed.
 *
 * Returning true causes the rebinding of the item, useful when you need to display state
 * changes (e.g. checkbox changing status from checked to unchecked)
 */
open fun onSelectionChanged(isNowSelected: Boolean): Boolean = true
```
But bear in mind that `AdapterItem` handles selected state for you already, so in many cases you shoudn't need to override this method. You can always access the current selection state with the `selected` boolean field available in all your custom `AdapterItem`s. Do not mess with `selected` and use it read-only.

3. Setup a click listener as you usually do in the `AdapterItem ViewHolder` and call the `setSelected` method:
```kotlin
toggleField.setOnClickListener {
    setSelected()
}
```

4. Setup the selection policy for your selection group:
```kotlin
recyclerAdapter.setSelectionGroupPolicy("MySelectionGroup", multiSelect = false)
```
Check Method's JavaDoc for full reference.

5. Add your custom selectable items as you usually do and that's it! For more information and a complete example, check the demo app provided and read the code in [SelectionActivity](https://github.com/gotev/recycler-adapter/blob/master/app/demo/src/main/java/net/gotev/recycleradapterdemo/SelectionActivity.kt) and [SelectableItem](https://github.com/gotev/recycler-adapter/blob/master/app/demo/src/main/java/net/gotev/recycleradapterdemo/adapteritems/SelectableItem.kt).

### Getting selected items in a selection group
To know which options the user selected:
```kotlin
val listOfSelectedItems = recyclerAdapter
    .getSelectedItems(selectionGroup = "MySelectionGroup") as List<YourCustomItem>
```
where `YourCustomItem` is the specific kind of items which you used for that particular custom selection group.

You can also dynamically observe selection changes by registering a listener:
```kotlin
recyclerAdapter.setSelectionGroupListener("MySelectionGroup", { group, selected ->
    val selectedItems = selected as List<YourCustomItem>
    Toast.makeText(this, "$group: $selectedItems", Toast.LENGTH_SHORT).show()
})
```

### Programmatically select items
To programmatically select an item:
```kotlin
recyclerAdapter.selectItem(yourItem)
```

### Replacing selection groups items and Master/Slave selection groups
Sometimes you may need replacing the items of a selection group with a new set. For example, imagine having two groups:

* First group where you can select one from:
    * 🍒 Fruits
    * 🥬 Vegetables
    * 🍮 Desserts

* Second group where the selections depends on the selections of the first group, so for example you want to have:
    * **Fruits ->** 🍏 Apple, 🍓 Strawberry and 🍒 Cherry
    * **Vegetables ->** 🥕 Carrot and 🥒 Cucumber
    * **Desserts ->** 🍰 Cake, 🍩 Donut and 🍦 Ice cream

This can be achieved combining `setSelectionGroupListener`, `replaceSelectionGroupItems` and `selectItem`. See [MasterSlaveGroupsActivity](https://github.com/gotev/recycler-adapter/blob/master/app/demo/src/main/java/net/gotev/recycleradapterdemo/MasterSlaveGroupsActivity.kt) for a complete example.

## <a name="leaveBehind"></a>Leave Behind pattern example implementation
In the demo app provided with the library, you can also see how to implement the [leave behind material design pattern](https://material.io/guidelines/components/lists-controls.html#lists-controls-types-of-list-controls). All the changes involved into the implementation can be seen in [this commit](https://github.com/gotev/recycler-adapter/commit/fa240519025f98ba609395034f42e89d5bb777fd). This implementation has not been included into the base library deliberately, to avoid depending on external libraries just for a single kind of item implementation. You can easily import the needed code in your project from the demo app sources if you want to have leave behind implementation.

## <a name="contributors"></a>Contributors
Thanks to:
* [Kristiyan Petrov](https://github.com/kristiyanP) for the beta testing and code review
* [Nicola Gallazzi](https://github.com/ngallazzi) for helping transitioning the library to AndroidX
