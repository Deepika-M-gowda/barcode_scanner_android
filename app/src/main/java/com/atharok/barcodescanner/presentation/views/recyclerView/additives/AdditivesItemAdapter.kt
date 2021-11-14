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

package com.atharok.barcodescanner.presentation.views.recyclerView.additives

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemAdditivesBinding
import com.atharok.barcodescanner.domain.entity.dependencies.Additive

class AdditivesItemAdapter(private val activity: Activity): RecyclerView.Adapter<AdditivesItemHolder>() {

    private val additivesList = mutableListOf<Additive>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdditivesItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemAdditivesBinding.inflate(layoutInflater, parent, false)

        return AdditivesItemHolder(activity, viewBinding)
    }

    override fun getItemCount(): Int = additivesList.size

    override fun onBindViewHolder(holder: AdditivesItemHolder, position: Int) {
        holder.updateItem(additivesList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun update(additivesList: List<Additive>){
        this.additivesList.clear()
        this.additivesList.addAll(additivesList)
        this.notifyDataSetChanged()
    }
}