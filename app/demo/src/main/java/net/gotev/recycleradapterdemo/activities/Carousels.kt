package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recycler_view.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.CarouselItem

class Carousels : AppCompatActivity(), RecyclerAdapterProvider {

    companion object {
        private const val PARAM_POOL = "withPool"
        fun show(activity: AppCompatActivity, withPool: Boolean) {
            activity.startActivity(Intent(activity, Carousels::class.java).apply {
                putExtra(PARAM_POOL, withPool)
            })
        }
    }

    override val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        val withPool = intent.getBooleanExtra(PARAM_POOL, false)

        title = getString(
            if (withPool) {
                R.string.carousels_pool
            } else {
                R.string.carousels_plain
            }
        )

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@Carousels, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        swipeRefresh.setOnRefreshListener {
            swipeRefresh.isRefreshing = false
        }

        val recycledViewPool = if (withPool) {
            RecyclerView.RecycledViewPool()
        } else {
            null
        }

        // 60 carousels with 40 items each
        render {
            (0..60).map { carouselNumber ->
                +CarouselItem(
                    title = "Carousel $carouselNumber",
                    adapter = renderableItems {
                        (0..40).map { itemNumber ->
                            +LabelItem("Text $itemNumber")
                        }
                    }.toAdapter(),
                    recycledViewPool = recycledViewPool
                )
            }
        }
    }
}
