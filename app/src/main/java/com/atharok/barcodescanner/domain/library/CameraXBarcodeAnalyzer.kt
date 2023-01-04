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

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.atharok.barcodescanner.common.extensions.toByteArray
import com.atharok.barcodescanner.presentation.customView.ScanOverlay
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import kotlin.math.roundToInt

class CameraXBarcodeAnalyzer(
    private val previewView: PreviewView,
    private val scanOverlay: ScanOverlay,
    private val onBarcodeDetected: (result: Result) -> Unit,
    private val onError: (text: String) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        val map = mapOf(
            DecodeHintType.POSSIBLE_FORMATS to BarcodeFormat.values().asList()
        )
        setHints(map)
    }

    override fun analyze(image: ImageProxy) {
        if(previewView.width == 0 || previewView.height==0)
            return

        val plane = image.planes[0]
        val imageData = plane.buffer.toByteArray()
        val rotationDegrees = image.imageInfo.rotationDegrees

        val values: Values = if (rotationDegrees == 0 || rotationDegrees == 180) {
            Values(
                byteArray = imageData,
                imageWidth = image.width,
                imageHeight = image.height,
                previewViewWidth = previewView.width,
                previewViewHeight = previewView.height,
                viewfinderWidth = scanOverlay.getViewfinderRect().width(),
                viewfinderHeight = scanOverlay.getViewfinderRect().height()
            )
        } else {
            Values(
                byteArray = rotateImageArray(imageData, image.width, image.height, rotationDegrees),
                imageWidth = image.height,
                imageHeight = image.width,
                previewViewWidth = previewView.height,
                previewViewHeight = previewView.width,
                viewfinderWidth = scanOverlay.getViewfinderRect().height(),
                viewfinderHeight = scanOverlay.getViewfinderRect().width()
            )
        }

        val scale = if (values.previewViewWidth < values.previewViewHeight) {
            (values.imageWidth / values.previewViewWidth.toFloat())
        }else{
            (values.imageHeight / values.previewViewHeight.toFloat())
        }

        val sizeX = values.viewfinderWidth * scale
        val sizeY = values.viewfinderHeight * scale

        val left = (values.imageWidth - sizeX) / 2f
        val top = (values.imageHeight - sizeY) / 2f

        try {
            val source = PlanarYUVLuminanceSource(
                values.byteArray,
                values.imageWidth, values.imageHeight,
                left.roundToInt(), top.roundToInt(),
                sizeX.roundToInt(), sizeY.roundToInt(),
                false
            )

            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            reader.reset()
            try {
                val result = reader.decode(binaryBitmap)
                onBarcodeDetected(result)
            } catch (e: ReaderException) {
                //e.printStackTrace() // Not Found
            }
        } catch (e: Exception) {
            onError(e.toString())
        }

        image.close()
    }

    // 90, 180. 270 rotation
    private fun rotateImageArray(byteArray: ByteArray, width: Int, height: Int, rotationDegrees: Int): ByteArray {
        if (rotationDegrees == 0) return byteArray
        if (rotationDegrees % 90 != 0) return byteArray

        val rotatedByteArray = ByteArray(byteArray.size)
        for (y in 0 until height) {
            for (x in 0 until width) {
                when (rotationDegrees) {
                    90 -> rotatedByteArray[x * height + height - y - 1] = byteArray[x + y * width]
                    180 -> rotatedByteArray[width * (height - y - 1) + width - x - 1] = byteArray[x + y * width]
                    270 -> rotatedByteArray[y + x * height] = byteArray[y * width + width - x - 1]
                }
            }
        }

        return rotatedByteArray
    }

    private data class Values(
        val byteArray: ByteArray,
        val imageWidth: Int,
        val imageHeight: Int,
        val previewViewWidth: Int,
        val previewViewHeight: Int,
        val viewfinderWidth: Int,
        val viewfinderHeight: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Values

            if (!byteArray.contentEquals(other.byteArray)) return false
            if (imageWidth != other.imageWidth) return false
            if (imageHeight != other.imageHeight) return false
            if (previewViewWidth != other.previewViewWidth) return false
            if (previewViewHeight != other.previewViewHeight) return false
            if (viewfinderWidth != other.viewfinderWidth) return false
            if (viewfinderHeight != other.viewfinderHeight) return false

            return true
        }

        override fun hashCode(): Int {
            var result = byteArray.contentHashCode()
            result = 31 * result + imageWidth
            result = 31 * result + imageHeight
            result = 31 * result + previewViewWidth
            result = 31 * result + previewViewHeight
            result = 31 * result + viewfinderWidth
            result = 31 * result + viewfinderHeight
            return result
        }
    }
}