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

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.atharok.barcodescanner.domain.entity.ImageFormat
import java.io.OutputStream

/**
 * Enregistre une image dans la mémoire interne de l'appareil.
 */
class BarcodeImageRecorder(private val context: Context) {

    fun saveBitmap(bitmap: Bitmap, imageFormat: ImageFormat, uri: Uri): Boolean {
        return when(imageFormat){
            ImageFormat.PNG -> saveAsImage(uri, saveAsPng(bitmap))
            ImageFormat.JPG -> saveAsImage(uri, saveAsJpg(bitmap))
            else -> false
        }
    }

    fun saveSvg(svg: String, imageFormat: ImageFormat, uri: Uri): Boolean {
        return when(imageFormat){
            ImageFormat.SVG -> saveAsImage(uri, saveAsSvg(svg))
            else -> false
        }
    }

    private fun saveAsImage(uri: Uri, action: (OutputStream) -> Unit): Boolean {
        var successful = true

        try {
            context.contentResolver.openOutputStream(uri)?.let { outputStream ->
                action(outputStream)
                outputStream.flush()
                outputStream.close()
            }
        } catch (e: Exception) {
            successful = false
            e.printStackTrace()
        }

        return successful
    }

    private fun saveAsPng(bitmap: Bitmap): (OutputStream) -> Unit = { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    }

    private fun saveAsJpg(bitmap: Bitmap): (OutputStream) -> Unit = { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    }

    private fun saveAsSvg(svg: String): (OutputStream) -> Unit = { outputStream ->
        outputStream.write(svg.toByteArray())
    }
}