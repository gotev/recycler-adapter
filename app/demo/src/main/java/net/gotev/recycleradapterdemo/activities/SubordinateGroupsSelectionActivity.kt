package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_selection.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.AdapterItems
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.adapterItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SelectableItem

class SubordinateGroupsSelectionActivity : AppCompatActivity(), RecyclerAdapterProvider {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SubordinateGroupsSelectionActivity::class.java))
        }
    }

    override val recyclerAdapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        title = getString(R.string.subordinate_groups_selection)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        recycler_view.apply {
            itemAnimator?.changeDuration = 0
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        action_button.apply {
            text = getString(R.string.finish_loading)

            setOnClickListener { button ->
                render(list(loading = false))
                button.visibility = View.GONE
            }
        }

        render(list(loading = true))
    }

    private fun list(
        loading: Boolean,
        selectedMainGroupItem: SelectableItem? = null
    ): AdapterItems = arrayListOf(
        LabelItem("Food categories"),

        *if (loading) {
            listOf(LabelItem("Loading ..."))
        } else {
            loadMainGroupItems(selectedMainGroupItem)
        }.toTypedArray(),

        *if (loading) {
            adapterItems()
        } else {
            adapterItems(
                LabelItem("Details"),

                *if (selectedMainGroupItem != null) {
                    loadSubordinateGroupItems(selectedMainGroupItem)
                } else {
                    adapterItems(LabelItem("Please select a food category first"))
                }.toTypedArray(),

                LabelItem("End of selection list")
            )
        }.toTypedArray()
    )

    private fun loadMainGroupItems(selectedItem: SelectableItem? = null): AdapterItems {
        val action: (SelectableItem) -> Unit = {
            render(list(loading = false, it))
        }

        fun SelectableItem.applySelection(selectedItem: SelectableItem?): SelectableItem {
            selected = equals(selectedItem)
            return this
        }

        return adapterItems(
            SelectableItem("\uD83C\uDF52 Fruits", action).applySelection(selectedItem),
            SelectableItem("\uD83E\uDD6C Vegetables", action).applySelection(selectedItem),
            SelectableItem("\uD83C\uDF6E Desserts", action).applySelection(selectedItem)
        )
    }

    private fun loadSubordinateGroupItems(selectedMainGroupItem: SelectableItem) = when {
        selectedMainGroupItem.label.contains("Fruits") -> adapterItems(
            SelectableItem("\uD83C\uDF4F Apple"),
            SelectableItem("\uD83C\uDF53 Strawberry"),
            SelectableItem("\uD83C\uDF52 Cherry")
        )

        selectedMainGroupItem.label.contains("Vegetables") -> adapterItems(
            SelectableItem("\uD83E\uDD55 Carrot"),
            SelectableItem("\uD83E\uDD52 Cucumber")
        )

        else -> adapterItems(
            SelectableItem("\uD83C\uDF70 Cake"),
            SelectableItem("\uD83C\uDF69 Donut"),
            SelectableItem("\uD83C\uDF66 Ice cream")
        )
    }
}
