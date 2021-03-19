package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_sync.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.adapterItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SyncItem
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SyncActivity : AppCompatActivity(), RecyclerAdapterProvider {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SyncActivity::class.java))
        }
    }

    override val recyclerAdapter = RecyclerAdapter()
    private var executor = ScheduledThreadPoolExecutor(1)
    private var scheduledOperation: ScheduledFuture<*>? = null

    private var listB = arrayListOf(
        SyncItem(1, "listA"),
        SyncItem(3, "listB"),
        SyncItem(4, "listB"),
        SyncItem(5, "listB")
    )

    private fun listB(): ArrayList<SyncItem> {
        listB.add(SyncItem(listB.last().id + 1, "listB${listB.last().id + 1}"))
        listB.add(SyncItem(listB.last().id + 1, "listB${listB.last().id + 1}"))
        return listB
    }

    private fun listA() = adapterItems(
        SyncItem(1, "listA"),
        SyncItem(2, "listA")
    )

    private fun listC() = adapterItems(
        SyncItem(1, "listC")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        title = getString(R.string.sync_with_items)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerAdapter.apply {
            setEmptyItem(LabelItem(getString(R.string.empty_list)))
            lockScrollingWhileInserting(linearLayoutManager)
        }

        recycler_view.apply {
            // fix blinking of first item when shuffling
            itemAnimator?.changeDuration = 0

            // normal setup
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }

        syncA.setOnClickListener {
            render(listA())
        }

        syncB.setOnClickListener {
            render(listB())
        }

        syncC.setOnClickListener {
            render(listC())
        }

        empty.setOnClickListener {
            recyclerAdapter.clear()
        }

        shuffle.setOnClickListener {
            scheduledOperation = if (scheduledOperation == null) {
                shuffle.text = getString(R.string.button_shuffle_stop)
                executor.scheduleAtFixedRate({
                    runOnUiThread {
                        render(ArrayList(createItems()))
                    }
                }, 1, 100, TimeUnit.MILLISECONDS)
            } else {
                shuffle.text = getString(R.string.button_shuffle_start)
                scheduledOperation?.cancel(true)
                null
            }
        }
    }

    override fun onPause() {
        super.onPause()
        scheduledOperation?.cancel(true)
        scheduledOperation = null
    }

    fun createItems(): List<AdapterItem<*>> {
        return (0..Random.nextInt(from = 2, until = 20)).flatMap {
            listOf(LabelItem("TITLE $it"), SyncItem(it, "ListC"))
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
            recyclerAdapter.sort(ascending = false)
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
