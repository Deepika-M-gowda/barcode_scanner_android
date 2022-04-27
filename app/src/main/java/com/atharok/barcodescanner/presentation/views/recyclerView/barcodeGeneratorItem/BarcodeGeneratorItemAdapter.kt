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

package com.atharok.barcodescanner.presentation.views.recyclerView.barcodeGeneratorItem
/*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemQrCreatorBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails

class BarcodeGeneratorItemAdapter(private val qrCodeCreatorTypeList: List<BarcodeFormatDetails>, private val callback: Listener): RecyclerView.Adapter<BarcodeGeneratorItemHolder>() {

    interface Listener {
        fun onClickItem(view: View?, barcodeFormatDetails: BarcodeFormatDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeGeneratorItemHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemQrCreatorBinding.inflate(inflater, parent, false)
        return BarcodeGeneratorItemHolder(viewBinding, callback)
    }

    override fun getItemCount(): Int = qrCodeCreatorTypeList.size

    override fun onBindViewHolder(holder: BarcodeGeneratorItemHolder, position: Int) {
        val barcodeFormatDetails: BarcodeFormatDetails = qrCodeCreatorTypeList[position]
        holder.update(barcodeFormatDetails)
    }
}*/