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
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import java.io.InputStream

/**
 * Recupère un VCard au format String à partir de son Uri.
 */
class VCardReader(private val context: Context) {

    fun readVCardFromContactUri(uri: Uri): String? {

        var vCardText: String? = null

        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        if (cursor?.moveToFirst() == true) {

            val columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY)
            val lookupKey: String = cursor.getString(columnIndex)

            val uriWithAppendPath: Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_VCARD_URI, lookupKey)

            try {
                val fd = context.contentResolver
                    .openAssetFileDescriptor(uriWithAppendPath, "r")

                if (fd != null) {
                    val inputStream: InputStream = fd.createInputStream()
                    vCardText = inputStream.readBytes().toString(Charsets.UTF_8)
                }
            } catch (e: Exception){
                //Log.e("Error", e.toString())
            }
        }
        cursor?.close()

        return vCardText
    }
}