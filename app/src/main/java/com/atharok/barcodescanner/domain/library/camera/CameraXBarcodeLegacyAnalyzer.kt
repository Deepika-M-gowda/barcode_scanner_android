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

import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.atharok.barcodescanner.common.extensions.toByteArray
import com.atharok.barcodescanner.presentation.customView.ScanOverlay
import com.google.zxing.Result
import kotlin.math.roundToInt

/**
 * For API 21 and 22.
 */
class CameraXBarcodeLegacyAnalyzer(
    private val previewView: PreviewView,
    private val scanOverlay: ScanOverlay,
    barcodeDetector: BarcodeDetector
) : AbstractCameraXBarcodeAnalyzer(barcodeDetector) {

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
                previewViewWidth = previewView.height,
                previewViewHeight = previewView.width,
                viewfinderSize = scanOverlay.viewfinderSize
            )
        } else {
            Values(
                byteArray = rotateImageArray(imageData, image.width, image.height, rotationDegrees),
                imageWidth = image.height,
                imageHeight = image.width,
                previewViewWidth = previewView.width,
                previewViewHeight = previewView.height,
                viewfinderSize = scanOverlay.viewfinderSize
            )
        }

        val scale = if (values.previewViewHeight < values.previewViewWidth) {
            (values.imageWidth / values.previewViewWidth.toFloat())
        }else{
            (values.imageHeight / values.previewViewHeight.toFloat())
        }

        val size = values.viewfinderSize * scale

        val left = (values.imageWidth - size) / 2f
        val top = (values.imageHeight - size) / 2f

        analyse(
            yuvData = values.byteArray,
            dataWidth = values.imageWidth,
            dataHeight = values.imageHeight,
            left = left.roundToInt(),
            top = top.roundToInt(),
            width = size.roundToInt(),
            height = size.roundToInt()
        )

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
        val viewfinderSize: Float
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
            if (viewfinderSize != other.viewfinderSize) return false

            return true
        }

        override fun hashCode(): Int {
            var result = byteArray.contentHashCode()
            result = 31 * result + imageWidth
            result = 31 * result + imageHeight
            result = 31 * result + previewViewWidth
            result = 31 * result + previewViewHeight
            result = 31 * result + viewfinderSize.hashCode()
            return result
        }
    }
}