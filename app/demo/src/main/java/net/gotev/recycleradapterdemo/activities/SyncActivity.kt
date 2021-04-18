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
import net.gotev.recycleradapter.ext.lockScrollingWhileInserting
import net.gotev.recycleradapter.ext.modifyItemsAndRender
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.Items
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sync)

        title = getString(R.string.sync_with_items)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerAdapter.lockScrollingWhileInserting(linearLayoutManager)

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
            recyclerAdapter.add(Items.label(getString(R.string.empty_list)))
        }

        recyclerAdapter.add(Items.label(getString(R.string.empty_list)))

        shuffle.setOnClickListener {
            scheduledOperation = if (scheduledOperation == null) {
                shuffle.text = getString(R.string.button_shuffle_stop)
                executor.scheduleAtFixedRate({
                    runOnUiThread {
                        render(createItems())
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

    private var listB = renderableItems {
        +Items.Card.sync(1, "listA")
        +Items.Card.sync(3, "listB")
        +Items.Card.sync(4, "listB")
        +Items.Card.sync(5, "listB")
    }

    private fun listB() = listB.apply {
        +Items.Card.sync((listB.last() as SyncItem).id + 1, "listB ${listB.last().diffingId()}")
        +Items.Card.sync((listB.last() as SyncItem).id + 1, "listB ${listB.last().diffingId()}")
    }

    private fun listA() = renderableItems {
        +Items.Card.sync(1, "listA")
        +Items.Card.sync(2, "listA")
    }

    private fun listC() = renderableItems {
        +Items.Card.sync(1, "listC")
    }

    fun createItems() = renderableItems {
        (0..Random.nextInt(from = 2, until = 20)).forEach {
            +Items.label("TITLE $it")
            +Items.Card.sync(it, "ListC")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.sort_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.sort_ascending -> {
            recyclerAdapter.modifyItemsAndRender { it.sorted() }
            true
        }

        R.id.sort_descending -> {
            recyclerAdapter.modifyItemsAndRender { it.sortedDescending() }
            true
        }

        else -> super.onOptionsItemSelected(item)
    }
}
