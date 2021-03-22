package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.CarouselItem
import net.gotev.recycleradapterdemo.adapteritems.Items
import net.gotev.recycleradapterdemo.adapteritems.LabelItem

class CarouselsActivity : RecyclerViewActivity() {

    companion object {
        private const val PARAM_POOL = "withPool"

        fun show(activity: AppCompatActivity, withPool: Boolean) {
            activity.startActivity(
                Intent(activity, CarouselsActivity::class.java).apply {
                    putExtra(PARAM_POOL, withPool)
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val withPool = intent.getBooleanExtra(PARAM_POOL, false)
        val recycledViewPool = if (withPool) RecyclerView.RecycledViewPool() else null

        title = getString(if (withPool) R.string.carousels_pool else R.string.carousels_plain)

        // 60 carousels with 40 items each
        render {
            (0..60).map { carouselNumber ->
                +Items.carousel(
                    title = "Carousel $carouselNumber",
                    adapter = renderableItems {
                        (0..40).map { itemNumber ->
                            +Items.label("Text $itemNumber")
                        }
                    }.toAdapter(),
                    recycledViewPool = recycledViewPool
                )
            }
        }
    }
}
