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

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * Configure tous les pré-requis permettant le partage d'une image.
 */
class BitmapSharer(private val context: Context) {

    fun share(bitmap: Bitmap): Uri? {
        val file = configureFile()
        val successful = writeBitmap(file, bitmap)
        return if (successful) FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file) else null
    }

    /**
     * Configure un fichier dans le répertoire cache
     */
    private fun configureFile(): File {
        val imagesFolder = File(context.cacheDir, "images")
        imagesFolder.mkdirs()
        return File(imagesFolder, "image.png")
    }

    /**
     * Enregistre le Bitmap dans le File (cache)
     */
    private fun writeBitmap(file: File, bitmap: Bitmap): Boolean {
        var successful = false
        try {
            val outputStream: OutputStream = FileOutputStream(file)
            successful = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return successful
    }
}