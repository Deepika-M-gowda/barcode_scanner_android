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

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Customisation de ItemTouchHelper.SimpleCallback
 *
 * Permet de déplacer uniquement le Foreground d'un item tout en laissant le Background fixe.
 * Pour cela on redéfinit les méthodes pour appliquer le Swipe uniquement au Foreground et non plus
 * à l'item complet.
 */
class CustomItemTouchHelperCallback(private val listener: ItemTouchHelperListener,
                                    dragDirs: Int,
                                    swipeDirs: Int)
    : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    interface ItemTouchHelperListener {
        fun getForegroundView(viewHolder: RecyclerView.ViewHolder?): View?
        fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
        fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return listener.onMove(recyclerView, viewHolder, target)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.bindingAdapterPosition)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        if(viewHolder!=null)
            getDefaultUIUtil().onSelected(listener.getForegroundView(viewHolder))
    }

    override fun onChildDrawOver(c: Canvas,
                                 recyclerView: RecyclerView,
                                 viewHolder: RecyclerView.ViewHolder?,
                                 dX: Float, dY: Float,
                                 actionState: Int,
                                 isCurrentlyActive: Boolean) {
        getDefaultUIUtil().onDrawOver(c, recyclerView, listener.getForegroundView(viewHolder),
            dX, dY, actionState, isCurrentlyActive)

    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        getDefaultUIUtil().clearView(listener.getForegroundView(viewHolder))
    }

    override fun onChildDraw(c: Canvas,
                             recyclerView: RecyclerView,
                             viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float,
                             actionState: Int,
                             isCurrentlyActive: Boolean) {
        getDefaultUIUtil().onDraw(c, recyclerView, listener.getForegroundView(viewHolder),
            dX, dY, actionState, isCurrentlyActive)
    }
}