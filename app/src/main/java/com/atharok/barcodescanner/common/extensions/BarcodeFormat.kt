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

package com.atharok.barcodescanner.common.extensions

import android.content.Context
import com.atharok.barcodescanner.R
import com.google.zxing.BarcodeFormat

fun BarcodeFormat.getDisplayName(context: Context): String = when (this) {
    BarcodeFormat.QR_CODE -> context.getString(R.string.barcode_qr_code_label)
    BarcodeFormat.DATA_MATRIX -> context.getString(R.string.barcode_data_matrix_label)
    BarcodeFormat.PDF_417 -> context.getString(R.string.barcode_pdf_417_label)
    BarcodeFormat.AZTEC -> context.getString(R.string.barcode_aztec_label)
    BarcodeFormat.EAN_13 -> context.getString(R.string.barcode_ean_13_label)
    BarcodeFormat.EAN_8 -> context.getString(R.string.barcode_ean_8_label)
    BarcodeFormat.UPC_A -> context.getString(R.string.barcode_upc_a_label)
    BarcodeFormat.UPC_E -> context.getString(R.string.barcode_upc_e_label)
    BarcodeFormat.CODE_128 -> context.getString(R.string.barcode_code_128_label)
    BarcodeFormat.CODE_93 -> context.getString(R.string.barcode_code_93_label)
    BarcodeFormat.CODE_39 -> context.getString(R.string.barcode_code_39_label)
    BarcodeFormat.CODABAR -> context.getString(R.string.barcode_codabar_label)
    BarcodeFormat.ITF -> context.getString(R.string.barcode_itf_label)
    else -> this.name.replace("_", " ")
}