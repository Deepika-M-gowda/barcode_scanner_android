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

import com.atharok.barcodescanner.common.extensions.is2DBarcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

/**
 * Génère l'image d'un code-barres à partir d'un texte.
 */
class BarcodeSvgGenerator(multiFormatWriter: MultiFormatWriter): BarcodeImageGenerator<String>(multiFormatWriter) {

    override fun createImageBarcode(
        content: String,
        barcodeFormat: BarcodeFormat,
        matrix: BitMatrix
    ): String {
        return if(barcodeFormat.is2DBarcode()) {
            create2DBarcodeSvg(matrix)
        } else {
            create1DBarcodeSvg(content, matrix) // Pour les codes à une dimension, on affiche le texte sous le code-barres.
        }
    }

    private fun create2DBarcodeSvg(matrix: BitMatrix): String {
        val barcodeImageWidth = matrix.width
        val barcodeImageHeight = matrix.height

        val builder = StringBuilder()
        buildSvgBegin(builder, totalWidth = barcodeImageWidth, totalHeight = barcodeImageHeight)
        buildSvgImageContent(builder, matrix = matrix)
        buildSvgEnd(builder)

        return builder.toString()
    }

    private fun create1DBarcodeSvg(content: String, matrix: BitMatrix): String {
        val barcodeImageWidth = matrix.width
        val barcodeImageHeight = matrix.width/2 // matrix.height == 1 car il s'agit d'un code-barres à une dimension. Donc pour avoir de la hauteur on met la hauteur = largeur divisé par 2.

        val textSize: Int = (barcodeImageWidth / (content.length + 2)) // Taille du texte s'affichant sous le code-barres.
        val totalHeight: Int = barcodeImageHeight+textSize // Image du code-barres + la hauteur du texte.

        val builder = StringBuilder()
        buildSvgBegin(builder, totalWidth = barcodeImageWidth, totalHeight = totalHeight)
        buildSvgImageContent(builder, matrix = matrix, bitWidth = 1, bitHeight = barcodeImageHeight)
        buildSvgTextContent(builder, content = content, textSize = textSize, posX = barcodeImageWidth / 2, posY = totalHeight)
        buildSvgEnd(builder)

        return builder.toString()
    }

    // build svg parts

    private fun buildSvgBegin(builder: StringBuilder, totalWidth: Int, totalHeight: Int) {
        builder.append("<svg width=\"$totalWidth\" height=\"$totalHeight\" viewBox=\"0 0 $totalWidth $totalHeight\" xmlns=\"http://www.w3.org/2000/svg\">\n")
    }

    private fun buildSvgEnd(builder: StringBuilder) {
        builder.append("</svg>\n")
    }

    private fun buildSvgImageContent(builder: StringBuilder, matrix: BitMatrix, bitWidth: Int = 1, bitHeight: Int = 1) {
        for (y in 0 until matrix.height) {
            for (x in 0 until matrix.width) {
                if (matrix.get(x, y)) {
                    builder.append("<rect x=\"$x\" y=\"$y\" width=\"$bitWidth\" height=\"$bitHeight\"/>\n")
                }
            }
        }
    }

    private fun buildSvgTextContent(builder: StringBuilder, content: String, textSize: Int, posX: Int, posY: Int) {
        builder.append("<text x=\"$posX\" y=\"$posY\" text-anchor=\"middle\" font-size=\"$textSize\">$content</text>\n")
    }
}