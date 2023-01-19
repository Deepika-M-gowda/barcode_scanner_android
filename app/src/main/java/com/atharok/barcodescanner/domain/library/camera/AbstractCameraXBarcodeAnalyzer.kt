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

package com.atharok.barcodescanner.domain.library.camera

import androidx.camera.core.ImageAnalysis
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

abstract class AbstractCameraXBarcodeAnalyzer(
    private val onBarcodeDetected: (result: Result) -> Unit,
    private val onError: (text: String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to BarcodeFormat.values().asList()
        )
        setHints(map)
    }

    protected fun analyse(yuvData: ByteArray,
                          dataWidth: Int,
                          dataHeight: Int,
                          left: Int,
                          top: Int,
                          width: Int,
                          height: Int) {

        try {
            val source = PlanarYUVLuminanceSource(
                yuvData,
                dataWidth, dataHeight,
                left, top,
                width, height,
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            reader.reset()
            try {
                val result = reader.decode(binaryBitmap)
                onBarcodeDetected(result)
            } catch (e: ReaderException) {
                val invertedSource = source.invert()
                val invertedBinaryBitmap = BinaryBitmap(HybridBinarizer(invertedSource))
                reader.reset()
                try {
                    val result = reader.decode(invertedBinaryBitmap)
                    onBarcodeDetected(result)
                } catch (e: ReaderException) {
                    //e.printStackTrace() // Not Found
                }
            }
        } catch (e: Exception) {
            onError(e.toString())
        }
    }
}