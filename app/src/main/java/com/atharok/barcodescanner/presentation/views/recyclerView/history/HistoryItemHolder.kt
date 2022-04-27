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

import android.text.format.DateUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.RecyclerViewItemHistoryBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.google.zxing.BarcodeFormat
import java.lang.ref.WeakReference

class HistoryItemHolder(private val viewBinding: RecyclerViewItemHistoryBinding)
    : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

    private var weakRefCallback: WeakReference<HistoryItemAdapter.OnItemClickListener>? = null
    private lateinit var barcode: Barcode

    init {
        itemView.setOnClickListener(this)
    }

    fun update(barcode: Barcode, listener: HistoryItemAdapter.OnItemClickListener){

        val barcodeType = barcode.getBarcodeType()

        // ---- Product Type Title TextView ----
        val entitled = if(barcode.name != "") barcode.name else itemView.context.getString(barcodeType.stringResource)
        viewBinding.recyclerViewItemHistoryEntitledTextView.text = entitled

        // ---- Product Type Icon ----
        val drawableResource: Int = when {
            barcodeType != BarcodeType.UNKNOWN -> barcodeType.drawableResource
            barcode.is1DProductBarcodeFormat || barcode.is1DIndustrialBarcodeFormat -> R.drawable.ic_bar_code_24
            else -> R.drawable.baseline_qr_code_24
        }

        viewBinding.recyclerViewItemHistoryImageView.setImageResource(drawableResource)

        // ---- Bar Code Icon ----
        val barcodeIconDrawableResource: Int = when {
            barcode.is1DProductBarcodeFormat || barcode.is1DIndustrialBarcodeFormat -> R.drawable.ic_bar_code_24
            barcode.is2DBarcodeFormat -> {
                when(barcode.getBarcodeFormat()){
                    BarcodeFormat.AZTEC -> R.drawable.ic_aztec_code_24
                    BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_code_24
                    BarcodeFormat.PDF_417 -> R.drawable.ic_pdf_417_code_24
                    else -> R.drawable.baseline_qr_code_24
                }
            }
            else -> R.drawable.baseline_qr_code_24
        }
        viewBinding.recyclerViewItemHistoryBarcodeIconImageView.setImageResource(barcodeIconDrawableResource)
        viewBinding.recyclerViewItemHistoryBarcodeFormatTextView.text = barcode.formatName.replace('_', ' ')

        // ---- Content barcode TextView ----
        viewBinding.recyclerViewItemHistoryContentTextView.text = barcode.contents

        // ---- Country ----
        val origin = barcode.country
        if(origin!=null){
            viewBinding.recyclerViewItemHistoryOriginFlagImageView.setImageResource(origin.drawableResource)
            viewBinding.recyclerViewItemHistoryOriginFlagImageView.visibility = View.VISIBLE
        }else{
            viewBinding.recyclerViewItemHistoryOriginFlagImageView.setImageDrawable(null)
            viewBinding.recyclerViewItemHistoryOriginFlagImageView.visibility = View.GONE
        }

        // ---- Date ----
        val date = barcode.scanDate
        viewBinding.recyclerViewItemHistoryDateTextView.text = getDateAgo(date)

        this.barcode=barcode
        this.weakRefCallback = WeakReference(listener)
    }

    override fun onClick(v: View?) {
        this.weakRefCallback?.get()?.onItemClick(v, barcode)
    }

    fun getForegroundLayout() = viewBinding.recyclerViewItemHistoryForegroundLayout
    //fun getBackgroundLayout() = viewBinding.recyclerViewItemHistoryBackgroundLayout

    /**
     * Permet d'avoir l'affichage textuel d'il y a combien de temps que le scan a eu lieu
     */
    private fun getDateAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val ago = DateUtils.getRelativeTimeSpanString(timestamp, now, DateUtils.MINUTE_IN_MILLIS)

        return ago.toString()
    }
}