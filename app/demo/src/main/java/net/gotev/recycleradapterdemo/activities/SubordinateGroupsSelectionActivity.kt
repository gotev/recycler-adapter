package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_selection.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.RenderableItems
import net.gotev.recycleradapter.ext.renderableItems
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
                render(groups(loading = false))
                button.visibility = View.GONE
            }
        }

        render(groups(loading = true))
    }

    private fun groups(
        loading: Boolean,
        selectedMainGroupItem: SelectableItem? = null
    ) = renderableItems {
        +LabelItem("Food categories")

        if (loading) {
            +LabelItem("Loading ...")
        } else {
            +mainGroupItems(selectedMainGroupItem)

            +LabelItem("Details")

            if (selectedMainGroupItem != null) {
                +subordinateGroupItems(selectedMainGroupItem)
            } else {
                +LabelItem("Please select a food category first")
            }

            +LabelItem("End of selection list")
        }
    }

    private fun mainGroupItems(selectedItem: SelectableItem? = null): RenderableItems {
        val action: (SelectableItem) -> Unit = {
            render(groups(loading = false, it))
        }

        fun SelectableItem.applySelection(selectedItem: SelectableItem?): SelectableItem {
            selected = equals(selectedItem)
            return this
        }

        return renderableItems {
            +SelectableItem("\uD83C\uDF52 Fruits", action).applySelection(selectedItem)
            +SelectableItem("\uD83E\uDD6C Vegetables", action).applySelection(selectedItem)
            +SelectableItem("\uD83C\uDF6E Desserts", action).applySelection(selectedItem)
        }
    }

    private fun subordinateGroupItems(selectedMainGroupItem: SelectableItem) = renderableItems {
        when {
            selectedMainGroupItem.label.contains("Fruits") -> {
                +SelectableItem("\uD83C\uDF4F Apple")
                +SelectableItem("\uD83C\uDF53 Strawberry")
                +SelectableItem("\uD83C\uDF52 Cherry")
            }

            selectedMainGroupItem.label.contains("Vegetables") -> {
                +SelectableItem("\uD83E\uDD55 Carrot")
                +SelectableItem("\uD83E\uDD52 Cucumber")
            }

            else -> {
                +SelectableItem("\uD83C\uDF70 Cake")
                +SelectableItem("\uD83C\uDF69 Donut")
                +SelectableItem("\uD83C\uDF66 Ice cream")
            }
        }
    }
}
