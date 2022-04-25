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

import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import kotlin.reflect.KClass

class JsonManager<T: Any>(file: File) {

    private val gson: Gson = Gson()
    private var jsonObject: JSONObject? = null

    init {
        try {
            val inputStream = file.inputStream()

            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()

            jsonObject = JSONObject(String(buffer))
        } catch (e: JSONException){
            e.printStackTrace()
        }
    }

    fun get(tag: String, kClass: KClass<T>): T? {

        try {

            // On récupère sous forme de chaine de caractères les données Json représentées par
            // l'attribut donné en paramètre
            val str = jsonObject?.getString(tag)

            // Récupère sous forme d'objet les données Json récupéré en String précédement
            return gson.fromJson(str, kClass.java)

        }catch (e: JSONException){
            e.printStackTrace()
        }
        return null
    }
}