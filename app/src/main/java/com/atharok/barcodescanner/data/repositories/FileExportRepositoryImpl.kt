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

import android.content.Context
import android.net.Uri
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.repositories.FileExportRepository
import com.google.gson.Gson
import java.io.OutputStream

class FileExportRepositoryImpl(private val context: Context): FileExportRepository {

    override fun exportToCsv(barcodes: List<Barcode>, uri: Uri): Boolean {
        return export(uri) { outputStream ->
            val strBuilder = StringBuilder()
            strBuilder.append("Barcode,Format,Scan Date,Type,Error Correction Level,Name\n")
            barcodes.forEach { barcode ->
                val contents = "\"${barcode.contents}\""
                val formatName = "\"${barcode.formatName}\""
                val scanDate = "\"${barcode.scanDate}\""
                val type = "\"${barcode.type}\""
                val errorCorrectionLevel = "\"${barcode.errorCorrectionLevel}\""
                val name = "\"${barcode.name}\""
                strBuilder.append("$contents,$formatName,$scanDate,$type,$errorCorrectionLevel,$name\n")
            }
            outputStream.write(strBuilder.toString().toByteArray())
        }
    }

    override fun exportToJson(barcodes: List<Barcode>, uri: Uri): Boolean {
        return export(uri) { outputStream ->
            val gson = Gson()
            outputStream.write(gson.toJson(barcodes).toByteArray())
        }
    }

    private fun export(uri: Uri, action: (OutputStream) -> Unit): Boolean {
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
}