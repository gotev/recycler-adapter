package net.gotev.recycleradapterdemo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recycler_view.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapterdemo.R

open class RecyclerViewActivity : AppCompatActivity(), RecyclerAdapterProvider {

    override val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_selection)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        recycler_view.apply {
            itemAnimator?.changeDuration = 0
            layoutManager = LinearLayoutManager(
                this@RecyclerViewActivity,
                RecyclerView.VERTICAL,
                false
            )
            adapter = recyclerAdapter
        }
    }
}
