package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.gotev.recycleradapter.ext.RenderableItems
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.Items
import net.gotev.recycleradapterdemo.adapteritems.SwitchItem

class SubordinateGroupsSelectionActivity : RecyclerViewActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SubordinateGroupsSelectionActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.subordinate_groups_selection)

        render(groups(loading = true))
    }

    private fun groups(
        loading: Boolean,
        selectedMainGroupItem: SwitchItem? = null
    ): RenderableItems = renderableItems {
        +Items.label("Food categories")

        if (loading) {
            +Items.label("Loading ...")
            +Items.button(
                text = getString(R.string.finish_loading),
                onClick = {
                    render(groups(loading = false))
                }
            )
        } else {
            +mainGroupItems(selectedMainGroupItem)

            +Items.label("Details")

            if (selectedMainGroupItem != null) {
                +subordinateGroupItems(selectedMainGroupItem)
            } else {
                +Items.label("Please select a food category first")
            }

            +Items.label("End of selection list")
        }
    }

    private fun mainGroupItems(selectedItem: SwitchItem? = null): RenderableItems {
        fun createMainGroupSwitchItem(label: String) = Items.switch(
            label = label,
            onClick = { item -> render(groups(loading = false, item)) }
        ).apply {
            selected = equals(selectedItem)
        }

        return renderableItems {
            +createMainGroupSwitchItem("\uD83C\uDF52 Fruits")
            +createMainGroupSwitchItem("\uD83E\uDD6C Vegetables")
            +createMainGroupSwitchItem("\uD83C\uDF6E Desserts")
        }
    }

    private fun subordinateGroupItems(selectedMainGroupItem: SwitchItem) = renderableItems {
        when {
            selectedMainGroupItem.label.contains("Fruits") -> {
                +Items.switch("\uD83C\uDF4F Apple")
                +Items.switch("\uD83C\uDF53 Strawberry")
                +Items.switch("\uD83C\uDF52 Cherry")
            }

            selectedMainGroupItem.label.contains("Vegetables") -> {
                +Items.switch("\uD83E\uDD55 Carrot")
                +Items.switch("\uD83E\uDD52 Cucumber")
            }

            else -> {
                +Items.switch("\uD83C\uDF70 Cake")
                +Items.switch("\uD83C\uDF69 Donut")
                +Items.switch("\uD83C\uDF66 Ice cream")
            }
        }
    }
}
