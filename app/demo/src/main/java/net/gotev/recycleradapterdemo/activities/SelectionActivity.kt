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
import net.gotev.recycleradapter.SelectionGroupListener
import net.gotev.recycleradapterdemo.R
import net.gotev.recycleradapterdemo.adapteritems.LabelItem
import net.gotev.recycleradapterdemo.adapteritems.SelectableItem


class SelectionActivity : AppCompatActivity() {

    companion object {
        private const val selectionGroupA = "selGroupA"
        private const val selectionGroupB = "selGroupB"

        fun show(activity: AppCompatActivity) {
            activity.startActivity(Intent(activity, SelectionActivity::class.java))
        }
    }

    private lateinit var groupListener: SelectionGroupListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection)

        title = getString(R.string.selection)

        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val recyclerAdapter = RecyclerAdapter()
        recyclerAdapter.setEmptyItem(LabelItem(getString(R.string.empty_list)))

        recycler_view.apply {
            itemAnimator?.changeDuration = 0
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            adapter = recyclerAdapter
        }

        groupListener = { group, selected ->
            Toast.makeText(
                    this@SelectionActivity,
                    "$group: ${selected.asString()}",
                    Toast.LENGTH_SHORT
            ).show()
        }

        recyclerAdapter.apply {
            setSelectionGroupPolicy(selectionGroupA, multiSelect = false)
            setSelectionGroupListener(selectionGroupA, groupListener)

            add(LabelItem(getString(R.string.single_selection)))

            add((1..3).map { SelectableItem("Option $it", selectionGroupA) })

            setSelectionGroupPolicy(selectionGroupB, multiSelect = true)
            setSelectionGroupListener(selectionGroupB, groupListener)

            add(LabelItem(getString(R.string.multiple_selection)))

            add((4..7).map { SelectableItem("Option $it", selectionGroupB) })
        }

        action_button.apply {
            text = getString(R.string.show_selections)

            setOnClickListener {
                val selectedA = recyclerAdapter.getSelectedAsString(selectionGroupA)
                val selectedB = recyclerAdapter.getSelectedAsString(selectionGroupB)

                AlertDialog.Builder(this@SelectionActivity)
                        .setTitle("Selected items")
                        .setMessage("${getString(R.string.single_selection)}:\n$selectedA\n\n" +
                                "${getString(R.string.multiple_selection)}:\n$selectedB")
                        .setPositiveButton("Ok", null)
                        .show()
            }
        }

    }

    private fun RecyclerAdapter.getSelectedAsString(selectionGroup: String) =
            getSelectedItems(selectionGroup).asString()

    private fun List<AdapterItem<*>>.asString() = joinToString { (it as SelectableItem).label }
            .let { if (it.isBlank()) "None" else it }

}
