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

package com.atharok.barcodescanner.data.file.image

import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Génère l'image d'un code-barres à partir d'un texte.
 */
abstract class BarcodeImageGenerator<T>(private val multiFormatWriter: MultiFormatWriter) {

    fun create(properties: BarcodeImageGeneratorProperties): T? {
        return try {
            val bitMatrix = encodeBarcodeImage(properties.contents, properties.format, properties.hints)
            createImageBarcode(properties, bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun encodeBarcodeImage(
        text: String,
        barcodeFormat: BarcodeFormat,
        hints: Map<EncodeHintType, Any>
    ): BitMatrix {
        return multiFormatWriter.encode(text, barcodeFormat, 0, 0, hints)
    }

    protected abstract fun createImageBarcode(properties: BarcodeImageGeneratorProperties, matrix: BitMatrix): T
}