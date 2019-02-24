package net.gotev.recycleradapterdemo.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_selection.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapter.SelectionGroupListener
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SelectableItem


class MasterSlaveGroupsActivity : AppCompatActivity() {

    companion object {
        private const val masterGroup = "master"
        private const val slaveGroup = "slave"

        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, MasterSlaveGroupsActivity::class.java))
        }
    }

    private lateinit var masterGroupListener: SelectionGroupListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        title = getString(R.string.selection_master_slave)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.setEmptyItem(LabelItem(getString(R.string.empty_list)))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        action_button.apply {
            text = getString(R.string.finish_loading)

            setOnClickListener { button ->
                val masterItems = loadMasterGroupItems()

                recyclerAdapter.apply {
                    replaceSelectionGroupItems(masterGroup, masterItems)
                    selectItem(masterItems.firstOrNull())
                }

                button.visibility = View.GONE
            }
        }

        masterGroupListener = { group, selected ->
            val selectedItem = selected.first() as SelectableItem
            Log.i("Selection", "Master group option selected is now: ${selectedItem.label}")
            recyclerAdapter.replaceSelectionGroupItems(slaveGroup, loadSlaveGroupItems(selectedItem))
        }

        recyclerAdapter.apply {
            setSelectionGroupPolicy(masterGroup, multiSelect = false)
            setSelectionGroupListener(masterGroup, masterGroupListener)

            add(LabelItem("Food categories"))

            add(LabelItem("Loading ...", masterGroup))

            setSelectionGroupPolicy(slaveGroup, multiSelect = false)

            add(LabelItem("Details"))

            add(LabelItem("Please select a food category first", slaveGroup))

            add(LabelItem("End of selection list"))
        }

    }

    private fun loadMasterGroupItems() =
            listOf(
                    SelectableItem("\uD83C\uDF52 Fruits", masterGroup),
                    SelectableItem("\uD83E\uDD6C Vegetables", masterGroup),
                    SelectableItem("\uD83C\uDF6E Desserts", masterGroup)
            )

    private fun loadSlaveGroupItems(masterItem: SelectableItem) = when {
        masterItem.label.contains("Fruits") -> listOf(
                SelectableItem("\uD83C\uDF4F Apple", slaveGroup),
                SelectableItem("\uD83C\uDF53 Strawberry", slaveGroup),
                SelectableItem("\uD83C\uDF52 Cherry", slaveGroup)
        )

        masterItem.label.contains("Vegetables") -> listOf(
                SelectableItem("\uD83E\uDD55 Carrot", slaveGroup),
                SelectableItem("\uD83E\uDD52 Cucumber", slaveGroup)
        )

        else -> listOf(
                SelectableItem("\uD83C\uDF70 Cake", slaveGroup),
                SelectableItem("\uD83C\uDF69 Donut", slaveGroup),
                SelectableItem("\uD83C\uDF66 Ice cream", slaveGroup)
        )
    }

}
