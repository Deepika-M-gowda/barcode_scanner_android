/*
 * Barcode Scanner
 * Copyright (C) 2021  Atharok
 *
 * This file is part of Barcode Scanner.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.atharok.barcodescanner.presentation.customView

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Solution pour gérer les marges de la RecyclerView à l'intérieur de celle-ci et pas à l'extérieur
 * comme avec layout_margin. Les marges s'appliquent autour des items à l'intérieur de la
 * RecyclerView et non autour de la RecyclerView elle-même.
 *
 * Implémentation:
 *      recyclerView.addItemDecoration(
 *          MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.margin))
 *      )
 *
 * Source de la solution : https://medium.com/@cesarmorigaki/a-better-way-to-set-recyclerview-items-margin-708ea9d3ac25
 */
class MarginItemDecoration(private val spaceSize: Int,
                           private val spanCount: Int = 1,
                           private val orientation: Int = LinearLayoutManager.VERTICAL)
    : RecyclerView.ItemDecoration(){

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        with(outRect) {
            if (orientation == LinearLayoutManager.VERTICAL) {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    top = spaceSize
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    left = spaceSize
                }
            } else {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    left = spaceSize
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    top = spaceSize
                }
            }

            right = spaceSize
            bottom = spaceSize
        }
    }
}