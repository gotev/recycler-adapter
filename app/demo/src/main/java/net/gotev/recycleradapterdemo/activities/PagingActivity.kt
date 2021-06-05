package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import net.gotev.recycleradapter.paging.PagingAdapter
import net.gotev.recycleradapterdemo.App
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.network.api.StarWarsPeopleDataSource

class PagingActivity : AppCompatActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, PagingActivity::class.java))
        }
    }

    private lateinit var pagingAdapter: PagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paging)

        title = getString(R.string.paged_scrolling)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        pagingAdapter = PagingAdapter(
            dataSource = { StarWarsPeopleDataSource(App.starWarsClient) },
            config = PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setMaxSize(50)
                .build()
        )

        findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@PagingActivity, RecyclerView.VERTICAL, false)
            adapter = pagingAdapter
        }

        findViewById<SwipeRefreshLayout>(R.id.swipeRefresh).apply {
            setOnRefreshListener {
                pagingAdapter.reload()
            }

            pagingAdapter.startObserving(
                this@PagingActivity,
                onLoadingComplete = { isRefreshing = false }
            )

            isRefreshing = true
        }
    }
}
