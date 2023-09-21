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
import android.net.Uri
import com.atharok.barcodescanner.data.file.FileExporter
import com.atharok.barcodescanner.data.file.image.BitmapSharer
import com.atharok.barcodescanner.domain.repositories.ImageExportRepository

class ImageExportRepositoryImpl(
    private val exporter: FileExporter,
    private val sharer: BitmapSharer
): ImageExportRepository {

    override fun exportToPng(bitmap: Bitmap, uri: Uri): Boolean {
        return exporter.export(uri) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    override fun exportToJpg(bitmap: Bitmap, uri: Uri): Boolean {
        return exporter.export(uri) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }

    override fun exportToSvg(svg: String, uri: Uri): Boolean {
        return exporter.export(uri) {
            it.write(svg.toByteArray())
        }
    }

    override fun shareBitmap(bitmap: Bitmap): Uri? {
        return sharer.share(bitmap)
    }
}