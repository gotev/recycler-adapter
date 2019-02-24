package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_api_integration.*
import net.gotev.recycleradapterdemo.App
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.network.api.StarWarsPeopleDataSource
import net.gotev.recycleradapterdemo.paging.PagingHelper


class APIIntegration : AppCompatActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, APIIntegration::class.java))
        }
    }

    private lateinit var pagingHelper: PagingHelper<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_integration)

        title = getString(R.string.api_integration)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        pagingHelper = PagingHelper(
                dataSource = { StarWarsPeopleDataSource(App.starWarsClient) },
                config = PagedList.Config.Builder()
                        .setPageSize(20)
                        .setEnablePlaceholders(false)
                        .setPrefetchDistance(10)
                        .setMaxSize(50)
                        .build()
        )

        recycler_view.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        pagingHelper.setupRecyclerView(recycler_view)

        swipeRefresh.isRefreshing = true
        pagingHelper.start(this) {
            swipeRefresh.isRefreshing = false
        }

        swipeRefresh.setOnRefreshListener {
            pagingHelper.reload()
        }

    }

}
