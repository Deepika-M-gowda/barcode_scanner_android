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

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Enregistre une image dans la mémoire interne de l'appareil.
 */
class BitmapRecorder(private val context: Context) {

    fun recordImage(bitmap: Bitmap): Boolean {

        val date = Date()
        val dateNameStr = SimpleDateFormat("yyyy-MM-dd-hh-mm-ss", Locale.getDefault()).format(date)
        val name = "barcode_$dateNameStr"

        val outputStream: OutputStream? = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            generateOutputStreamForAndroidQAndHigher(name, date)
        else
            generateOutputStreamForAndroidPAndLower(name)

        val successful = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        outputStream?.flush()
        outputStream?.close()

        return successful
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun generateOutputStreamForAndroidQAndHigher(name: String, date: Date): OutputStream? {
        val resolver: ContentResolver = context.contentResolver
        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.png")
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
        contentValues.put(MediaStore.MediaColumns.DATE_ADDED, date.time / 1000)
        contentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, date.time / 1000)
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        return if(imageUri != null) resolver.openOutputStream(imageUri) else null

    }

    @Suppress("DEPRECATION")
    private fun generateOutputStreamForAndroidPAndLower(name: String): OutputStream {
        val imagesFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
        val image = File(imagesFolder, "$name.png")
        return FileOutputStream(image)
    }
}