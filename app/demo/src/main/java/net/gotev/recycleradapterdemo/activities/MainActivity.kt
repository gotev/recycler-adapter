package net.gotev.recycleradapterdemo.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.enableDragDrop
import net.gotev.recycleradapter.ext.modifyItemsAndRender
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.Items
import net.gotev.recycleradapterdemo.adapteritems.LabelWithToggleItem
import net.gotev.recycleradapterdemo.adapteritems.TitleSubtitleItem
import java.util.Random
import java.util.UUID

class MainActivity : AppCompatActivity(), RecyclerAdapterProvider {

    private val random by lazy { Random(System.currentTimeMillis()) }

    override val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerAdapter.setEmptyItem(Items.label(getString(R.string.empty_list)))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
            recyclerAdapter.enableDragDrop(this)
        }

        configureActions()

        render {
            +Items.leaveBehind(
                value = "swipe to left to reveal options",
                background = "delete",
                onClick = { item -> recyclerAdapter.removeItem(item) }
            )

            // only non-null elements of lists and arrays are rendered
            +listOf(
                null,
                Items.leaveBehind(
                    value = "swipe to left to reveal options 2",
                    background = "delete",
                    onClick = { item -> recyclerAdapter.removeItem(item) }
                )
            )

            +arrayListOf(
                Items.leaveBehind(
                    value = "swipe to left to reveal options 3",
                    background = "delete",
                    onClick = { item -> recyclerAdapter.removeItem(item) }
                ),
                null,
                Items.leaveBehind(
                    value = "swipe to left to reveal options 4",
                    background = "delete",
                    onClick = { item -> recyclerAdapter.removeItem(item) }
                )
            )

            +arrayOf(
                null,
                Items.leaveBehind(
                    value = "swipe to left to reveal options 5",
                    background = "delete",
                    onClick = { item -> recyclerAdapter.removeItem(item) }
                )
            )

            (0..random.nextInt(200) + 50).map {
                if (it % 2 == 0)
                    +Items.Card.titleSubtitle("Item $it", "subtitle $it")
                else
                    +Items.Card.labelWithToggle("Toggle $it")
            }
        }
    }

    private fun configureActions() {
        add_item.setOnClickListener {
            recyclerAdapter.add(
                Items.Card.titleSubtitle("Item ${UUID.randomUUID()}", "subtitle"),
                position = 1
            )
        }

        remove_all.setOnClickListener {
            recyclerAdapter.clear()
        }

        remove_last_item_of_a_kind.setOnClickListener {
            // remove last item with class TextWithToggleItem
            recyclerAdapter.modifyItemsAndRender { items ->
                items.apply {
                    remove(lastOrNull { it::class.java == LabelWithToggleItem::class.java })
                }
            }
        }

        remove_all_items_of_a_kind.setOnClickListener {
            // remove all items with class TitleSubtitleItem
            recyclerAdapter.modifyItemsAndRender { items ->
                items.filter { it::class.java != TitleSubtitleItem::class.java }
            }
        }
    }

    private fun onSearch(query: String?) {
        recyclerAdapter.filter(query)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        (menu.findItem(R.id.search).actionView as SearchView).apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    onSearch(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    onSearch(newText)
                    return false
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sync_demo -> {
            SyncActivity.show(this)
            true
        }

        R.id.selection -> {
            GroupsSelectionActivity.show(this)
            true
        }

        R.id.selection_multi_groups -> {
            SubordinateGroupsSelectionActivity.show(this)
            true
        }

        R.id.async_loading -> {
            AsyncLoadingActivity.show(this)
            true
        }

        R.id.api_integration -> {
            PagingActivity.show(this)
            true
        }

        R.id.carousels_plain -> {
            CarouselsActivity.show(this, withPool = false)
            true
        }

        R.id.carousels_pool -> {
            CarouselsActivity.show(this, withPool = true)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
