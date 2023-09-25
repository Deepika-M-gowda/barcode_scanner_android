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

package com.atharok.barcodescanner.data.file

import android.content.Context
import android.net.Uri
import java.io.InputStream
import java.io.OutputStream

class FileStream(private val context: Context) {
    fun export(uri: Uri, action: (OutputStream) -> Unit): Boolean {
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

    fun import(uri: Uri, action: (InputStream) -> Unit): Boolean {
        var successful = true
        try {
            context.contentResolver.openInputStream(uri)?.let { outputStream ->
                action(outputStream)
                outputStream.close()
            }
        } catch (e: Exception) {
            successful = false
            e.printStackTrace()
        }
        return successful
    }
}