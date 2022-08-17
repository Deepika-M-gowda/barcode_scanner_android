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

package com.atharok.barcodescanner.presentation.views.recyclerView.history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemHistoryBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode

class HistoryItemAdapter(private val callback: OnItemClickListener): RecyclerView.Adapter<HistoryItemHolder>() {

    interface OnItemClickListener {
        fun onItemClick(view: View?, barcode: Barcode)
    }

    private var barcodeList = listOf<Barcode>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemHistoryBinding.inflate(inflater, parent, false)
        return HistoryItemHolder(viewBinding)
    }

    override fun getItemCount(): Int = barcodeList.size

    override fun onBindViewHolder(holder: HistoryItemHolder, position: Int) {
        holder.update(barcodeList[position], callback)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(barcodeList: List<Barcode>){
        /*this.barcodeMutableList.clear()
        this.barcodeMutableList.addAll(barcodeList)*/
        this.barcodeList = barcodeList
        this.notifyDataSetChanged()
    }

    fun getItem(position: Int) = this.barcodeList[position]
}