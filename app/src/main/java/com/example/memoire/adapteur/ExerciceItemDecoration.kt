package com.example.memoire.adapteur

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ExerciceItemDecoration:RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        //cree un margine
       outRect.bottom=20
    }
}