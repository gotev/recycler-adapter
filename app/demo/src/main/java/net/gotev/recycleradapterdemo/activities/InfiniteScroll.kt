package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_recycler_view.recycler_view
import kotlinx.android.synthetic.main.activity_recycler_view.swipeRefresh
import net.gotev.recycleradapter.paging.PagingAdapter
import net.gotev.recycleradapterdemo.App
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.network.api.StarWarsPeopleDataSource

class InfiniteScroll : AppCompatActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, InfiniteScroll::class.java))
        }
    }

    private lateinit var pagingAdapter: PagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)

        title = getString(R.string.infinite_scrolling)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        pagingAdapter = PagingAdapter(
            owner = this,
            dataSource = { StarWarsPeopleDataSource(App.starWarsClient) },
            config = PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setMaxSize(50)
                .build(),
            onLoadingComplete = { swipeRefresh.isRefreshing = false }
        )

        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.adapter = pagingAdapter

        swipeRefresh.isRefreshing = true

        swipeRefresh.setOnRefreshListener {
            pagingAdapter.reload()
        }

    }

}
