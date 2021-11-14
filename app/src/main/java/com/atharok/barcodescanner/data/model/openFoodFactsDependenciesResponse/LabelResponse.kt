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

package com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class LabelResponse(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("known")
    @Expose
    val known: Int? = null,

    @SerializedName("name")
    @Expose
    val name: String? = null,

    @SerializedName("products")
    @Expose
    val products: Int? = null,

    @SerializedName("sameAs")
    @Expose
    val sameAs: List<String?>? = null,

    @SerializedName("url")
    @Expose
    val url: String? = null,

    @SerializedName("image")
    @Expose
    val image: String? = null
)