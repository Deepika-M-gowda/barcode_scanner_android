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

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.atharok.barcodescanner.common.extensions.is2DBarcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Génère l'image d'un code-barres à partir d'un texte.
 */
class BarcodeBitmapGenerator(multiFormatWriter: MultiFormatWriter): BarcodeImageGenerator<Bitmap>(multiFormatWriter) {

    override fun createImageBarcode(
        content: String,
        barcodeFormat: BarcodeFormat,
        matrix: BitMatrix
    ): Bitmap {
        val barcodeImageWidth = matrix.width
        val barcodeImageHeight = matrix.height

        val bitmap: Bitmap

        if(barcodeFormat.is2DBarcode()) {
            bitmap = Bitmap.createBitmap(barcodeImageWidth, barcodeImageHeight, Bitmap.Config.ARGB_8888)
        } else {
            val textSize: Int = (barcodeImageWidth / (content.length + 2)) // ajuster la taille du texte en fonction de la largeur de l'image et du contenu
            bitmap = Bitmap.createBitmap(barcodeImageWidth, barcodeImageHeight + textSize, Bitmap.Config.ARGB_8888)
            createContentImage(bitmap, content, textSize.toFloat(), barcodeImageWidth, barcodeImageHeight)
        }

        createBarcodeImage(bitmap, matrix)

        return bitmap
    }

    private fun createBarcodeImage(bitmap: Bitmap, matrix: BitMatrix) {
        for (x in 0 until matrix.width) {
            for (y in 0 until matrix.height) {
                bitmap.setPixel(x, y, if (matrix[x, y]) Color.BLACK else Color.WHITE)
            }
        }
    }

    private fun createContentImage(bitmap: Bitmap, content: String, textSize: Float, width: Int, height: Int) {
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE) // dessiner un fond blanc
        val paint = Paint().apply {
            this.color = Color.BLACK
            this.textSize = textSize
            this.textAlign = Paint.Align.CENTER
        }
        val textRect = Rect()
        paint.getTextBounds(content, 0, content.length, textRect)
        val textHeight = textRect.height().toFloat()
        canvas.drawRect(0f, height.toFloat(), width.toFloat(), height + textHeight + 10f, paint.apply { color = Color.WHITE }) // dessiner un rectangle blanc en dessous du texte
        canvas.drawText(content, width/2f, height + textSize - 10f, paint.apply { color = Color.BLACK }) // afficher le code texte
    }
}