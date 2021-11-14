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

package com.atharok.barcodescanner.data.model.openLibraryResponse.records.details

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class DetailsMain(
    @SerializedName("info_url")
    @Expose
    val infoUrl: String? = null,

    @SerializedName("bib_key")
    @Expose
    val bibKey: String? = null,
    
    @SerializedName("preview_url")
    @Expose
    val previewUrl: String? = null,
    
    @SerializedName("thumbnail_url")
    @Expose
    val thumbnailUrl: String? = null,
    
    @SerializedName("details")
    @Expose
    val details: Details? = null,
    
    @SerializedName("preview")
    @Expose
    val preview: String? = null
)