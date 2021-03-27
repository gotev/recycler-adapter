package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.Items
import net.gotev.recycleradapterdemo.network.AsyncState

class AsyncLoadingActivity : RecyclerViewActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, AsyncLoadingActivity::class.java))
        }
    }

    private val model by lazy {
        ViewModelProvider(this).get(AsyncLoadingViewModel::class.java)
    }

    private fun reloadButton() = Items.button("Reload", onClick = {
        model.people.fetch(Unit)
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.async_loading)

        model.people.status.observe(this) { status ->
            when (status) {
                is AsyncState.Loading -> {
                    render {
                        +Items.label("Loading ...")
                    }
                }

                is AsyncState.Success -> {
                    render {
                        if (status.data.results.isEmpty()) {
                            +Items.label("No results")
                            +reloadButton()
                        } else {
                            +reloadButton()
                            status.data.results.forEach { person ->
                                +Items.Card.titleSubtitle(
                                    title = person.name,
                                    subtitle = "Height (cm): ${person.height}"
                                )
                            }
                        }
                    }
                }

                is AsyncState.Error -> {
                    render {
                        +Items.label("An error occurred!")
                        +Items.label(status.error.toString())
                        +reloadButton()
                    }
                }
            }
        }

        model.people.fetch(Unit)
    }
}
