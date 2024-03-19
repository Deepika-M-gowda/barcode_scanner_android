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

package com.atharok.barcodescanner.presentation.views.recyclerView.customUrl

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.getColorStateListFromAttrRes
import com.atharok.barcodescanner.databinding.RecyclerViewItemCustomUrlBinding
import java.lang.ref.WeakReference

class CustomUrlItemHolder(private val viewBinding: RecyclerViewItemCustomUrlBinding)
    : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener, View.OnLongClickListener {

    private var weakRefCallback: WeakReference<CustomUrlItemAdapter.OnCustomUrlItemListener>? = null
    private lateinit var item: CustomUrlItem

    init {
        viewBinding.recyclerViewItemCustomUrlForegroundLayout.apply {
            setOnClickListener(this@CustomUrlItemHolder)
            setOnLongClickListener(this@CustomUrlItemHolder)
        }
    }

    fun update(item: CustomUrlItem, listener: CustomUrlItemAdapter.OnCustomUrlItemListener){
        viewBinding.recyclerViewItemCustomUrlNameTextView.text = item.customUrl.name
        viewBinding.recyclerViewItemCustomUrlLinkTextView.text = item.customUrl.url

        // ---- Item CardView Selected ----
        getForegroundLayout().backgroundTintList = if(item.isSelected){
            itemView.context.getColorStateListFromAttrRes(R.attr.colorSurfaceVariant)
        } else null

        this.item = item
        this.weakRefCallback = WeakReference(listener)
    }

    fun getForegroundLayout() = viewBinding.recyclerViewItemCustomUrlForegroundLayout

    override fun onClick(v: View?) {
        this.weakRefCallback?.get()?.let { bankItemListener ->
            if(bankItemListener.isSelectedMode()) {
                selectItem(v, bankItemListener)
            } else {
                bankItemListener.onItemClick(v, item.customUrl)
            }
        }
    }

    override fun onLongClick(v: View?): Boolean {
        this.weakRefCallback?.get()?.let { barcodeItemListener ->
            selectItem(v, barcodeItemListener)
        }
        return true
    }

    private fun selectItem(v: View?, barcodeItemListener: CustomUrlItemAdapter.OnCustomUrlItemListener) {
        item.isSelected = !item.isSelected
        barcodeItemListener.onItemSelect(v, item.customUrl, item.isSelected)
        getForegroundLayout().backgroundTintList = if(item.isSelected){
            itemView.context.getColorStateListFromAttrRes(R.attr.colorSurfaceVariant)
        } else null
    }
}