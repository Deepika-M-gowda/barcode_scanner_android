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

package com.atharok.barcodescanner.data.model.openLibraryResponse.items

import androidx.annotation.Keep
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.commons.CoverSchema
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Item(
    @SerializedName("status")
    @Expose
    val status: String? = null,

    @SerializedName("ol-work-id")
    @Expose
    val olWorkId: String? = null,
    
    @SerializedName("ol-edition-id")
    @Expose
    val olEditionId: String? = null,
    
    @SerializedName("cover")
    @Expose
    val cover: CoverSchema? = null,
    
    @SerializedName("publishDate")
    @Expose
    val publishDate: String? = null,
    
    @SerializedName("itemURL")
    @Expose
    val itemURL: String? = null,
    
    @SerializedName("enumcron")
    @Expose
    val enumcron: Boolean? = null,
    
    @SerializedName("contributor")
    @Expose
    val contributor: String? = null,
    
    @SerializedName("fromRecord")
    @Expose
    val fromRecord: String? = null,
    
    @SerializedName("match")
    @Expose
    val match: String? = null
)