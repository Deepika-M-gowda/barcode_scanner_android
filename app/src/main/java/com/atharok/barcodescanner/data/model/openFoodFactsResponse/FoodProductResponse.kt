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
data class FoodProductResponse(
    @SerializedName("product_name")
    @Expose
    val productName: String? = null,

    @SerializedName("brands")
    @Expose
    val brands: String? = null,

    @SerializedName("quantity")
    @Expose
    val quantity: String? = null,

    @SerializedName("image_front_url")
    @Expose
    val imageFrontUrl: String? = null,

    @SerializedName("ingredients_analysis_tags")
    @Expose
    val ingredientsAnalysisTags: List<String>? = null,

    @SerializedName("nutrition_grades")
    @Expose
    val nutritionGrades: String? = null,

    @SerializedName("nova_group")
    @Expose
    val novaGroup: Int? = null,

    @SerializedName("ecoscore_grade")
    @Expose
    val ecoScoreGrade: String? = null,

    @SerializedName("categories")
    @Expose
    val categories: String? = null,

    @SerializedName("packaging")
    @Expose
    val packaging: String? = null,

    @SerializedName("stores")
    @Expose
    val stores: String? = null,

    @SerializedName("countries_tags")
    @Expose
    val countriesTags: List<String>? = null,

    @SerializedName("origins_tags")
    @Expose
    val originsTags: List<String>? = null,

    @SerializedName("ingredients_text_with_allergens")
    @Expose
    val ingredientsTextWithAllergens: String? = null,

    @SerializedName("ingredients_text")
    @Expose
    val ingredientsText: String? = null,

    @SerializedName("ingredients_text_fr")
    @Expose
    val ingredientsTextFr: String? = null,

    @SerializedName("ingredients_text_with_allergens_fr")
    @Expose
    val ingredientsTextWithAllergensFr: String? = null,

    @SerializedName("allergens_tags")
    @Expose
    val allergensTags: List<String>? = null,

    @SerializedName("traces_tags")
    @Expose
    val tracesTags: List<String>? = null,

    @SerializedName("additives_tags")
    @Expose
    val additivesTags: List<String>? = null,

    @SerializedName("ingredients")
    @Expose
    val ingredientsResponses: List<IngredientResponse>? = null,

    @SerializedName("nutriments")
    @Expose
    val nutrimentsResponse: NutrimentsResponse? = null,

    @SerializedName("nutrition_score_beverage")
    @Expose
    val nutritionScoreBeverage: Int? = null,

    @SerializedName("serving_quantity")
    @Expose
    val servingQuantity: Double? = null,

    @SerializedName("labels")
    @Expose
    val labels: String? = null,

    @SerializedName("labels_tags")
    @Expose
    val labelsTags: List<String>? = null
)