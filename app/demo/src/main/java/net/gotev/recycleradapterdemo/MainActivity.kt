package net.gotev.recycleradapterdemo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.TextWithToggleItem
import net.gotev.recycleradapterdemo.adapteritems.TitleSubtitleItem
import net.gotev.recycleradapterdemo.adapteritems.leavebehind.MyLeaveBehindItem
import java.util.*


class MainActivity : AppCompatActivity() {

    private val random by lazy {
        Random(System.currentTimeMillis())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.setEmptyItem(LabelItem(getString(R.string.empty_list)))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
            recyclerAdapter.enableDragDrop(this)
        }

        recyclerAdapter.add(MyLeaveBehindItem("swipe to left to leave behind", "option"))

        val items = (0..random.nextInt(200) + 50).map {
            if (it % 2 == 0)
                TitleSubtitleItem(this, "Item $it")
            else
                TextWithToggleItem("Item $it")
        }

        recyclerAdapter.add(items)

        remove_all_items_of_a_kind.setOnClickListener {
            recyclerAdapter.removeAllItemsWithClass(TitleSubtitleItem::class.java)
        }

        remove_last_item_of_a_kind.setOnClickListener {
            recyclerAdapter.removeLastItemWithClass(TextWithToggleItem::class.java)
        }

        add_item.setOnClickListener {
            recyclerAdapter.add(TitleSubtitleItem(this, "added item " + UUID.randomUUID().toString()))
        }

        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                recyclerAdapter.filter(search.text.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.sync_demo -> {
            SyncActivity.show(this)
            true
        }

        R.id.selection -> {
            SelectionActivity.show(this)
            true
        }

        R.id.selection_multi_groups -> {
            MasterSlaveGroupsActivity.show(this)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
