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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutBdd

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemAboutBinding

class BddItemAdapter: RecyclerView.Adapter<BddItemHolder>() {

    private val bddArray: Array<Bdd> = Bdd.entries.toTypedArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BddItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val viewBinding = RecyclerViewItemAboutBinding.inflate(layoutInflater, parent, false)

        return BddItemHolder(viewBinding)
    }

    override fun getItemCount(): Int = bddArray.size

    override fun onBindViewHolder(holder: BddItemHolder, position: Int) {
        holder.updateItem(bddArray[position])
    }
}