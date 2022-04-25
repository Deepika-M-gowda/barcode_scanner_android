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

package com.atharok.barcodescanner.domain.library

import android.graphics.Bitmap
import com.atharok.barcodescanner.common.utils.ENCODING_ISO_8859_1
import com.atharok.barcodescanner.common.utils.ENCODING_UTF_8
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

/**
 * Génère l'image d'un code-barres à partir d'un texte.
 */
class BitmapBarcodeGenerator(private val multiFormatWriter: MultiFormatWriter,
                             private val barcodeEncoder: BarcodeEncoder) {

    fun create(text: String, barcodeFormat: BarcodeFormat, width: Int, height: Int): Bitmap? {

        return try {
            val bitMatrix = createBitMatrix(text, barcodeFormat, width, height)
            barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createBitMatrix(text: String, barcodeFormat: BarcodeFormat, width: Int, height: Int): BitMatrix {
        val encoding: String = when(barcodeFormat){
            BarcodeFormat.QR_CODE, BarcodeFormat.PDF_417 -> ENCODING_UTF_8
            else -> ENCODING_ISO_8859_1
        }

        val hints = mapOf<EncodeHintType, Any>(EncodeHintType.CHARACTER_SET to encoding)
        return multiFormatWriter.encode(text, barcodeFormat, width, height, hints)
    }
}