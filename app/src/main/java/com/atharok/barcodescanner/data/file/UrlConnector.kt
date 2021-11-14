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

import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class UrlConnector {

    private var input: BufferedInputStream? = null

    fun openConnection(urlStr: String): BufferedInputStream? {

        try {
            val url = URL(urlStr)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.connect()
            input = BufferedInputStream(url.openStream())

        } catch (e: Exception){
            input=null
            //Log.e("Exception", e.toString())
        }

        return input
    }

    fun closeConnection(){
        input?.close()
    }
}