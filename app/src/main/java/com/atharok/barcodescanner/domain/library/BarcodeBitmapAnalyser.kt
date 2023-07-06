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
import android.util.Log
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.ReaderException
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer


/**
 * Recherche un code-barres dans une image.
 */
class BarcodeBitmapAnalyser {

    private val reader = MultiFormatReader()

    fun detectBarcodeFromBitmap(bitmap: Bitmap): Result? {

        val width = bitmap.width
        val height = bitmap.height
        val size = width * height

        val bitmapBuffer = IntArray(size)

        bitmap.getPixels(bitmapBuffer, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, bitmapBuffer)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        //val hints = hashMapOf<DecodeHintType, Any>()
        //hints[DecodeHintType.TRY_HARDER] = true
        //hints[DecodeHintType.PURE_BARCODE] = true

        reader.reset()

        return try {
            reader.decode(binaryBitmap)
        } catch (e: NotFoundException) {
            val invertedSource = source.invert()
            val invertedBinaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
            reader.reset()
            try {
                reader.decode(invertedBinaryBitmap)
            } catch (e: ReaderException) {
                Log.e("BitmapBarcodeAnalyser", "Barcode not found in Bitmap")
                null
            }
        }
    }
}