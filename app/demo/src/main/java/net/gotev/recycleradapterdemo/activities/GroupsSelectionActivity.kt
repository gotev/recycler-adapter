package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_selection.*
import net.gotev.recycleradapter.AdapterItem
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.ext.AdapterItems
import net.gotev.recycleradapter.ext.RecyclerAdapterProvider
import net.gotev.recycleradapter.ext.adapterItems
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SelectableItem

class GroupsSelectionActivity : AppCompatActivity(), RecyclerAdapterProvider {

    companion object {
        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, GroupsSelectionActivity::class.java))
        }
    }

    override val recyclerAdapter = RecyclerAdapter()
    private var groupAselected: List<SelectableItem> = emptyList()
    private var groupBselected: List<SelectableItem> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        title = getString(R.string.groups_selection)

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
            text = getString(R.string.show_selections)

            setOnClickListener {
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
        }

        render(selectionGroups())
    }

    private fun selectionGroups() = adapterItems(
        LabelItem(getString(R.string.single_selection)),
        *singleSelectionGroup().toTypedArray(),

        LabelItem(getString(R.string.multiple_selection)),
        *multipleSelectionGroup().toTypedArray()
    )

    private fun singleSelectionGroup(): AdapterItems {
        val selectedItem = groupAselected.firstOrNull()

        fun SelectableItem.applySelection(selectedItem: SelectableItem?) = apply {
            selected = equals(selectedItem)
        }

        val action: (item: SelectableItem) -> Unit = {
            val selected = listOf(it)
            onGroupChangedSelection("Group A", selected)
            groupAselected = selected
            render(selectionGroups())
        }

        return adapterItems(
            SelectableItem("Option 1", action).applySelection(selectedItem),
            SelectableItem("Option 2", action).applySelection(selectedItem),
            SelectableItem("Option 3", action).applySelection(selectedItem)
        )
    }

    private fun multipleSelectionGroup(): AdapterItems {
        val selectedItems = groupBselected

        fun SelectableItem.applySelection() = apply {
            selected = selectedItems.contains(this)
        }

        val action: (item: SelectableItem) -> Unit = { item ->
            val selected = ArrayList(groupBselected)
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

        return adapterItems(
            SelectableItem("Option 4", action).applySelection(),
            SelectableItem("Option 5", action).applySelection(),
            SelectableItem("Option 6", action).applySelection()
        )
    }

    private fun onGroupChangedSelection(group: String, selected: List<AdapterItem<*>>) {
        Toast.makeText(this, "$group: ${selected.asString()}", Toast.LENGTH_SHORT).show()
    }

    private fun List<AdapterItem<*>>.asString() = joinToString { (it as SelectableItem).label }
        .let { if (it.isBlank()) "None" else it }
}
