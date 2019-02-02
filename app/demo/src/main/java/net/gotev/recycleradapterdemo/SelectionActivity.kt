package net.gotev.recycleradapterdemo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_selection.*
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapterdemo.adapteritems.EmptyItem
import net.gotev.recycleradapterdemo.adapteritems.SelectableItem


class SelectionActivity : AppCompatActivity() {

    companion object {
        private const val selectionGroupA = "selGroupA"
        private const val selectionGroupB = "selGroupB"

        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SelectionActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        title = getString(R.string.selection)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.setEmptyItem(EmptyItem(getString(R.string.empty_list)))

        recycler_view.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        recyclerAdapter.apply {
            setSelectionGroupPolicy(SelectionActivity.selectionGroupA, multiSelect = false)

            add(EmptyItem(getString(R.string.single_selection)))

            add(SelectableItem("Option A", SelectionActivity.selectionGroupA))
            add(SelectableItem("Option B", SelectionActivity.selectionGroupA))
            add(SelectableItem("Option C", SelectionActivity.selectionGroupA))

            setSelectionGroupPolicy(SelectionActivity.selectionGroupB, multiSelect = true)

            add(EmptyItem(getString(R.string.multiple_selection)))

            add(SelectableItem("Option D", SelectionActivity.selectionGroupB))
            add(SelectableItem("Option E", SelectionActivity.selectionGroupB))
            add(SelectableItem("Option F", SelectionActivity.selectionGroupB))
            add(SelectableItem("Option G", SelectionActivity.selectionGroupB))
        }

        show_selections.setOnClickListener {
            val selectedA = recyclerAdapter.getSelectedAsString(SelectionActivity.selectionGroupA)
            val selectedB = recyclerAdapter.getSelectedAsString(SelectionActivity.selectionGroupB)

            AlertDialog.Builder(this)
                    .setTitle("Selected items")
                    .setMessage("${getString(R.string.single_selection)}:\n$selectedA\n\n" +
                            "${getString(R.string.multiple_selection)}:\n$selectedB")
                    .setPositiveButton("Ok", null)
                    .show()
        }

    }

    private fun RecyclerAdapter.getSelectedAsString(selectionGroup: String) =
            getSelectedItems(selectionGroup)
                    .joinToString { (it as SelectableItem).label }
                    .let { if (it.isBlank()) "None" else it }

}
