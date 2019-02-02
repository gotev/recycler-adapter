package net.gotev.recycleradapterdemo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_sync.*
import net.gotev.recycleradapter.RecyclerAdapter


class SyncActivity : AppCompatActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SyncActivity::class.java))
        }
    }

    private lateinit var recyclerAdapter: RecyclerAdapter

    private fun listA() =
            arrayListOf(
                    SyncItem(this, 1, "listA"),
                    SyncItem(this, 2, "listA"),
                    SyncItem(this, 3, "listA")
            )

    private fun listB() =
            arrayListOf(
                    SyncItem(this, 1, "listB"),
                    SyncItem(this, 4, "listB"),
                    SyncItem(this, 5, "listB")
            )

    private fun listC() =
            arrayListOf(
                    SyncItem(this, 1, "listC")
            )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        title = getString(R.string.sync_with_items)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.setEmptyItem(EmptyItem(getString(R.string.empty_list)))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        syncA.setOnClickListener {
            recyclerAdapter.syncWithItems(listA())
        }

        syncB.setOnClickListener {
            recyclerAdapter.syncWithItems(listB())
        }

        syncC.setOnClickListener {
            recyclerAdapter.syncWithItems(listC())
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sort_ascending -> {
            recyclerAdapter.sort(ascending = true)
            true
        }

        R.id.sort_descending -> {
            recyclerAdapter.sort(false)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

}
