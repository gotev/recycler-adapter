package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_sync.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapterdemo.App
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.TitleSubtitleItem


class APIIntegration : AppCompatActivity() {

    private val disposeBag by lazy {
        CompositeDisposable()
    }

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, APIIntegration::class.java))
        }
    }

    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_integration)

        title = getString(R.string.api_integration)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        recyclerAdapter = RecyclerAdapter().apply {
            setEmptyItem(LabelItem(getString(R.string.loading)))
        }

        recycler_view.apply {
            layoutManager = linearLayoutManager
            adapter = recyclerAdapter
        }
    }

    override fun onResume() {
        super.onResume()

        App.starWarsClient.getStarships()
                .map { response ->
                    response.results.map { starship ->
                        TitleSubtitleItem(starship.name, starship.manufacturer)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ items ->
                    recyclerAdapter.add(items)
                }, {
                    Log.e("Error", "Error while loading starhips", it)
                }).autoDisposeOnPause()
    }

    private fun Disposable.autoDisposeOnPause() {
        disposeBag.add(this)
    }

    override fun onPause() {
        super.onPause()
        disposeBag.dispose()
    }

}
