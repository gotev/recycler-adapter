package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.ext.RenderableItems
import net.gotev.recycleradapter.ext.renderableItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.ButtonItem
import net.gotev.recycleradapterdemo.adapteritems.Items
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SwitchItem

class GroupsSelectionActivity : RecyclerViewActivity() {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, GroupsSelectionActivity::class.java))
        }
    }

    private var groupAselected = emptyList<SwitchItem>()
    private var groupBselected = emptyList<SwitchItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = getString(R.string.groups_selection)

        render(selectionGroups())
    }

    private fun selectionGroups() = renderableItems {
        +Items.label(getString(R.string.single_selection))
        +singleSelectionGroup()

        +Items.label(getString(R.string.multiple_selection))
        +multipleSelectionGroup()

        +Items.button(
            text = getString(R.string.show_selections),
            onClick = {
                val selectedA = groupAselected.asString()
                val selectedB = groupBselected.asString()

                AlertDialog.Builder(this@GroupsSelectionActivity)
                    .setTitle("Selected items")
                    .setMessage(
                        "${getString(R.string.single_selection)}:\n$selectedA\n\n" +
                            "${getString(R.string.multiple_selection)}:\n$selectedB"
                    )
                    .setPositiveButton("Ok", null)
                    .show()
            }
        )
    }

    private fun singleSelectionGroup(): RenderableItems = renderableItems {
        val selectedItem = groupAselected.firstOrNull()

        (1..3).map { number ->
            +Items.switch(
                label = "Option $number",
                onClick = { item ->
                    val selected = listOf(item)
                    onGroupChangedSelection("Group A", selected)
                    groupAselected = selected
                    render(selectionGroups())
                }
            ).apply {
                selected = equals(selectedItem)
            }
        }
    }

    private fun multipleSelectionGroup(): RenderableItems = renderableItems {
        val selectedItems = groupBselected

        (4..6).map { number ->
            +Items.switch(
                label = "Option $number",
                onClick = { item ->
                    val selected = ArrayList(selectedItems)
                    val isCurrentlySelected = selected.any { it == item }

                    if (isCurrentlySelected) {
                        selected.remove(item)
                    } else {
                        selected.add(item)
                    }

                    onGroupChangedSelection("Group B", selected)
                    groupBselected = selected
                    render(selectionGroups())
                }
            ).apply {
                selected = selectedItems.contains(this)
            }
        }
    }

    private fun onGroupChangedSelection(group: String, selected: List<AdapterItem<*>>) {
        Toast.makeText(this, "$group: ${selected.asString()}", Toast.LENGTH_SHORT).show()
    }

    private fun List<AdapterItem<*>>.asString() = joinToString { (it as SwitchItem).label }
        .let { if (it.isBlank()) "None" else it }
}
