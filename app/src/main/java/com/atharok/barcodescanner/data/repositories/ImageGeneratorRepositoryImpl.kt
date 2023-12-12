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

package com.atharok.barcodescanner.data.repositories

import android.graphics.Bitmap
import com.atharok.barcodescanner.data.file.image.BarcodeBitmapGenerator
import com.atharok.barcodescanner.data.file.image.BarcodeSvgGenerator
import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.atharok.barcodescanner.domain.repositories.ImageGeneratorRepository

class ImageGeneratorRepositoryImpl(
    private val bitmapGenerator: BarcodeBitmapGenerator,
    private val svgGenerator: BarcodeSvgGenerator
): ImageGeneratorRepository {

    override fun createBitmap(properties: BarcodeImageGeneratorProperties): Bitmap? {
        return bitmapGenerator.create(properties)
    }

    override fun createSvg(properties: BarcodeImageGeneratorProperties): String? {
        return svgGenerator.create(properties)
    }
}