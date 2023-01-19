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

class CameraXBarcodeAnalyzer(
    private val previewView: PreviewView,
    private val scanOverlay: ScanOverlay,
    onBarcodeDetected: (result: Result) -> Unit,
    onError: (text: String) -> Unit
) : AbstractCameraXBarcodeAnalyzer(onBarcodeDetected, onError) {

    override fun analyze(image: ImageProxy) {
        if(previewView.width == 0 || previewView.height==0)
            return

        val plane = image.planes[0]
        val imageData = plane.buffer.toByteArray()
        val rotationDegrees = image.imageInfo.rotationDegrees

        val previewWidth: Int
        val previewHeight: Int

        if (rotationDegrees == 0 || rotationDegrees == 180) {
            previewWidth = previewView.width
            previewHeight = previewView.height
        } else {
            previewWidth = previewView.height
            previewHeight = previewView.width
        }

        val scale = if (previewWidth < previewHeight) {
            image.width / previewWidth.toFloat()
        }else{
            image.height / previewHeight.toFloat()
        }

        val size = scanOverlay.viewfinderSize * scale

        val left = (image.width - size) / 2f
        val top = (image.height - size) / 2f

        analyse(
            yuvData = imageData,
            dataWidth = image.width,
            dataHeight = image.height,
            left = left.roundToInt(),
            top = top.roundToInt(),
            width = size.roundToInt(),
            height = size.roundToInt()
        )

        image.close()
    }
}