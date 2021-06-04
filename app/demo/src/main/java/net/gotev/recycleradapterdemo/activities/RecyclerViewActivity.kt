package net.gotev.recycleradapterdemo.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.lockScrollingWhileInserting
import net.gotev.recycleradapterdemo.R

open class RecyclerViewActivity : AppCompatActivity(), RecyclerAdapterProvider {

    override val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_recycler_view)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            val layout = LinearLayoutManager(
                this@RecyclerViewActivity,
                RecyclerView.VERTICAL,
                false
            )
            itemAnimator?.changeDuration = 0
            layoutManager = layout
            adapter = recyclerAdapter

            recyclerAdapter.lockScrollingWhileInserting(layout)
        }
    }
}
