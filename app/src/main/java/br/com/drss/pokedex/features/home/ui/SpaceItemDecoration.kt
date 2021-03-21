package br.com.drss.pokedex.features.home.ui

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val margin: Int, val columnCount: Int): RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.apply {
            left = margin
            right = margin
            bottom = margin
            top = if(parent.getChildAdapterPosition(view) + 1 > columnCount) margin else 0
        }
    }
}