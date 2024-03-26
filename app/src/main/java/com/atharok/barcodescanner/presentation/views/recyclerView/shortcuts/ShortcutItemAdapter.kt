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

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemShortcutBinding
import com.atharok.barcodescanner.domain.entity.DynamicShortcut

class ShortcutItemAdapter: RecyclerView.Adapter<ShortcutItemHolder>() {

    val items = mutableListOf<DynamicShortcut>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShortcutItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemShortcutBinding.inflate(inflater, parent, false)
        return ShortcutItemHolder(viewBinding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ShortcutItemHolder, position: Int) {
        holder.update(items[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(items: List<DynamicShortcut>){
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = items.removeAt(fromPosition)
        items.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }
}