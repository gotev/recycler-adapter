package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recycler_view.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.NestedRecyclerAdapterItem
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.TitledCarousel


class Carousels : AppCompatActivity() {

    companion object {
        private const val PARAM_POOL = "withPool"
        fun show(activity: AppCompatActivity, withPool: Boolean) {
            activity.startActivity(Intent(activity, Carousels::class.java).apply {
                putExtra(PARAM_POOL, withPool)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val withPool = intent.getBooleanExtra(PARAM_POOL, false)

        title = getString(if (withPool) {
            R.string.carousels_pool
        } else {
            R.string.carousels_plain
        })

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerAdapter = RecyclerAdapter()

        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.adapter = recyclerAdapter

        val recycledViewPool = if (withPool) {
            RecyclerAdapter.createRecycledViewPool(
                    parent = recycler_view,
                    items = listOf(LabelItem("")),
                    maxViewsPerItem = 50
            )
        } else {
            null
        }

        recyclerAdapter.add(createCarousels(recycledViewPool))

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }

    }

    private fun createCarouselItems(): List<LabelItem> {
        return (0..40).map {
            LabelItem("Text $it")
        }
    }

    private fun createCarousels(recycledViewPool: RecyclerView.RecycledViewPool?)
            : List<NestedRecyclerAdapterItem<*,*>> {
        return (0..60).map {
            val adapter = RecyclerAdapter().apply {
                add(createCarouselItems())
            }

            TitledCarousel("Carousel $it", adapter, recycledViewPool)
        }
    }

}
