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

package com.atharok.barcodescanner.presentation.views.recyclerView.shortcuts

import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemShortcutBinding
import com.atharok.barcodescanner.domain.entity.DynamicShortcut

class ShortcutItemHolder(private val viewBinding: RecyclerViewItemShortcutBinding)
    : RecyclerView.ViewHolder(viewBinding.root) {

    fun update(shortcut: DynamicShortcut){
        val context = itemView.context

        viewBinding.root.let { cardView ->
            cardView.viewTreeObserver.addOnGlobalLayoutListener {
                val height = cardView.height.toFloat()
                val cornerRadius = height / 2f
                cardView.radius = cornerRadius
            }
        }

        viewBinding.recyclerViewItemShortcutImageView.setImageResource(shortcut.icon)
        viewBinding.recyclerViewItemShortcutLabelTextView.text = context.getString(shortcut.label)
    }
}