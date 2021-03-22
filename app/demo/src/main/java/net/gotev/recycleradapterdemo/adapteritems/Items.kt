package net.gotev.recycleradapterdemo.adapteritems

import androidx.recyclerview.widget.RecyclerView
import net.gotev.recycleradapter.RecyclerAdapter
import net.gotev.recycleradapterdemo.adapteritems.leavebehind.MyLeaveBehindItem

/**
 * Convenience factory to create all the items and favor dot-click programming
 * using Items-dot-something which makes the IDE display the available options.
 */
object Items {

    object Card {
        fun titleSubtitle(title: String, subtitle: String) = TitleSubtitleItem(title, subtitle)

        fun sync(id: Int, suffix: String) = SyncItem(id, suffix)

        fun labelWithToggle(text: String) = LabelWithToggleItem(text)
    }

    fun button(text: String, onClick: (() -> Unit)? = null) = ButtonItem(text, onClick)

    fun carousel(
        title: String,
        adapter: RecyclerAdapter,
        recycledViewPool: RecyclerView.RecycledViewPool?
    ) = CarouselItem(title, adapter, recycledViewPool)

    fun label(text: String) = LabelItem(text)

    fun switch(label: String, onClick: ((item: SwitchItem) -> Unit)? = null) =
        SwitchItem(label, onClick)

    fun leaveBehind(value: String, background: String) = MyLeaveBehindItem(value, background)
}
