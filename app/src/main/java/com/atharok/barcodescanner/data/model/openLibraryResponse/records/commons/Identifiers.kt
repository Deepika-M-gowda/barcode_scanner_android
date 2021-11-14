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

package com.atharok.barcodescanner.data.model.openLibraryResponse.records.commons

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Identifiers(
    @SerializedName("openlibrary")
    @Expose
    val openlibrary: List<String>? = null,

    @SerializedName("isbn_13")
    @Expose
    val isbn13: List<String>? = null,
    
    @SerializedName("amazon")
    @Expose
    val amazon: List<String>? = null,
    
    @SerializedName("isbn_10")
    @Expose
    val isbn10: List<String>? = null,
    
    @SerializedName("oclc")
    @Expose
    val oclc: List<String>? = null,
    
    @SerializedName("goodreads")
    @Expose
    val goodreads: List<String>? = null,
    
    @SerializedName("librarything")
    @Expose
    val librarything: List<String>? = null
)