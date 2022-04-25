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
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

/**
 * Recherche un code-barres dans une image.
 */
class BitmapBarcodeAnalyser {

    fun findBarcodeInBitmap(bitmap: Bitmap): Result? {

        val width = bitmap.width
        val height = bitmap.height
        val size = width * height

        val bitmapBuffer = IntArray(size)

        bitmap.getPixels(bitmapBuffer, 0, width, 0, 0, width, height)

        val source = RGBLuminanceSource(width, height, bitmapBuffer)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        val reader = MultiFormatReader()

        return try {
            reader.decode(binaryBitmap)
        } catch (e: NotFoundException) {
            Log.e("BitmapBarcodeAnalyser", "Barcode not found in Bitmap")
            null
        }
    }
}