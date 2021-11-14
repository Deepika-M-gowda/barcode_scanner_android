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

package com.atharok.barcodescanner.data.model.openLibraryResponse.records

import androidx.annotation.Keep
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.data.Data
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.details.DetailsMain
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Book(
    @SerializedName("recordURL")
    @Expose
    val recordURL: String? = null,

    @SerializedName("oclcs")
    @Expose
    val oclcs: List<String>? = null,
    
    @SerializedName("publishDates")
    @Expose
    val publishDates: List<String>? = null,
    
    @SerializedName("lccns")
    @Expose
    val lccns: List<Any>? = null,
    
    @SerializedName("details")
    @Expose
    val details: DetailsMain? = null,
    
    @SerializedName("isbns")
    @Expose
    val isbns: List<String>? = null,
    
    @SerializedName("olids")
    @Expose
    val olids: List<String>? = null,
    
    @SerializedName("issns")
    @Expose
    val issns: List<Any>? = null,
    
    @SerializedName("data")
    @Expose
    val data: Data? = null
)