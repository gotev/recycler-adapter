package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.lockScrollingWhileInserting
import net.gotev.recycleradapter.ext.modifyItemsAndRender
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.Items
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

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            // fix blinking of first item when shuffling
            itemAnimator?.changeDuration = 0

            // normal setup
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }

        findViewById<MaterialButton>(R.id.syncA).setOnClickListener {
            render(listA())
        }

        findViewById<MaterialButton>(R.id.syncB).setOnClickListener {
            render(listB())
        }

        findViewById<MaterialButton>(R.id.syncC).setOnClickListener {
            render(listC())
        }

        findViewById<MaterialButton>(R.id.empty).setOnClickListener {
            render {
                +Items.label(getString(R.string.empty_list))
            }
        }

        render {
            +Items.label(getString(R.string.empty_list))
        }

        findViewById<MaterialButton>(R.id.shuffle).apply {
            setOnClickListener {
                scheduledOperation = if (scheduledOperation == null) {
                    text = getString(R.string.button_shuffle_stop)
                    executor.scheduleAtFixedRate({
                        runOnUiThread {
                            render(createItems())
                        }
                    }, 1, 100, TimeUnit.MILLISECONDS)
                } else {
                    text = getString(R.string.button_shuffle_start)
                    scheduledOperation?.cancel(true)
                    null
                }
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
