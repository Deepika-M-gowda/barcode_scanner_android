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
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.commons.EnValue
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.commons.LanguageValue
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CountryResponse (

    @SerializedName("country_code_2")
    @Expose
    val countryCode2: EnValue? = null,

    @SerializedName("languages")
    @Expose
    val languages: EnValue? = null,

    @SerializedName("country_code_3")
    @Expose
    val countryCode3: EnValue? = null,

    @SerializedName("name")
    @Expose
    val name: LanguageValue? = null
)