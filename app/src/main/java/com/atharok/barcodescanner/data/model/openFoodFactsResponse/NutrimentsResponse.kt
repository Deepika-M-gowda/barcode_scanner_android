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

package com.atharok.barcodescanner.data.model.openFoodFactsResponse

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class NutrimentsResponse(
    // ---- Energy (kj) ----

    @SerializedName("energy_100g")
    @Expose
    val energyKj100g: Double? = null,

    @SerializedName("energy_serving")
    @Expose
    val energyKjServing: Double? = null,
    
    val energyKjUnit: String = "kJ",
    
    // ---- Energy (kcal) ----
    
    @SerializedName("energy-kcal_100g")
    @Expose
    val energyKcal100g: Double? = null,
    
    @SerializedName("energy-kcal_serving")
    @Expose
    val energyKcalServing: Double? = null,
    
    @SerializedName("energy-kcal_unit")
    @Expose
    val energyKcalUnit: String? = null,
    
    // ---- Fat ----
    
    @SerializedName("fat_100g")
    @Expose
    val fat100g: Double? = null,
    
    @SerializedName("fat_serving")
    @Expose
    val fatServing: Double? = null,
    
    @SerializedName("fat_unit")
    @Expose
    val fatUnit: String? = null,
    
    // ---- Saturated Fat ----
    
    @SerializedName("saturated-fat_100g")
    @Expose
    val saturatedFat100g: Double? = null,
    
    @SerializedName("saturated-fat_serving")
    @Expose
    val saturatedFatServing: Double? = null,
    
    @SerializedName("saturated-fat_unit")
    @Expose
    val saturatedFatUnit: String? = null,
    
    // ---- Carbohydrates ----
    
    @SerializedName("carbohydrates_100g")
    @Expose
    val carbohydrates100g: Double? = null,
    
    @SerializedName("carbohydrates_serving")
    @Expose
    val carbohydratesServing: Double? = null,
    
    @SerializedName("carbohydrates_unit")
    @Expose
    val carbohydratesUnit: String? = null,
    
    // ---- Sugars ----
    
    @SerializedName("sugars_100g")
    @Expose
    val sugars100g: Double? = null,
    
    @SerializedName("sugars_serving")
    @Expose
    val sugarsServing: Double? = null,
    
    @SerializedName("sugars_unit")
    @Expose
    val sugarsUnit: String? = null,
    
    // ---- Starch ----
    
    @SerializedName("starch_100g")
    @Expose
    val starch100g: Double? = null,
    
    @SerializedName("starch_serving")
    @Expose
    val starchServing: Double? = null,
    
    @SerializedName("starch_unit")
    @Expose
    val starchUnit: String? = null,
    
    // ---- Fiber ----
    
    @SerializedName("fiber_100g")
    @Expose
    val fiber100g: Double? = null,
    
    @SerializedName("fiber_serving")
    @Expose
    val fiberServing: Double? = null,
    
    @SerializedName("fiber_unit")
    @Expose
    val fiberUnit: String? = null,
    
    // ---- Proteins ----
    
    @SerializedName("proteins_100g")
    @Expose
    val proteins100g: Double? = null,
    
    @SerializedName("proteins_serving")
    @Expose
    val proteinsServing: Double? = null,
    
    @SerializedName("proteins_unit")
    @Expose
    val proteinsUnit: String? = null,
    
    // ---- Salt ----
    
    @SerializedName("salt_100g")
    @Expose
    val salt100g: Double? = null,
    
    @SerializedName("salt_serving")
    @Expose
    val saltServing: Double? = null,
    
    @SerializedName("salt_unit")
    @Expose
    val saltUnit: String? = null,
    
    // ---- Sodium ----
    
    @SerializedName("sodium_100g")
    @Expose
    val sodium100g: Double? = null,
    
    @SerializedName("sodium_serving")
    @Expose
    val sodiumServing: Double? = null,
    
    @SerializedName("sodium_unit")
    @Expose
    val sodiumUnit: String? = null
)