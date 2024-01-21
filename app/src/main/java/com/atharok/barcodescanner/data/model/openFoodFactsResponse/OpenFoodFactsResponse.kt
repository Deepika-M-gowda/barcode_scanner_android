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
import com.atharok.barcodescanner.common.extensions.polishText
import com.atharok.barcodescanner.domain.entity.analysis.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPI
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.foodProduct.*
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class OpenFoodFactsResponse(
    @SerializedName("status")
    @Expose
    var status: Int = 0,

    @SerializedName("code")
    @Expose
    var code: String? = null,

    @SerializedName("product")
    @Expose
    var productResponse: FoodProductResponse? = null
) {

    fun toModel(barcode: Barcode, source: RemoteAPI): FoodBarcodeAnalysis = FoodBarcodeAnalysis(
        barcode = barcode,
        source = source,
        name = productResponse?.productName?.polishText(),
        brands = productResponse?.brands?.polishText()?.polishText(),
        quantity = productResponse?.quantity?.polishText(),
        imageFrontUrl = productResponse?.imageFrontUrl,
        categories = productResponse?.categories?.polishText(),
        labels = productResponse?.labels?.polishText(),
        labelsTagList = productResponse?.labelsTags,
        packaging = productResponse?.packaging?.polishText(),
        stores = productResponse?.stores?.polishText(),
        salesCountriesTagsList = productResponse?.countriesTags,
        originsCountriesTagsList = productResponse?.originsTags,
        nutriscore = getNutriscore(productResponse),
        novaGroup = getNovaGroup(productResponse),
        ecoScore = getEcoScore(productResponse),
        ingredients = productResponse?.ingredientsTextWithAllergens ?: productResponse?.ingredientsText ?: productResponse?.ingredientsTextWithAllergensFr ?: productResponse?.ingredientsTextFr,
        tracesTagsList = productResponse?.tracesTags,
        allergensTagsList = productResponse?.allergensTags,
        additivesTagsList = productResponse?.additivesTags,
        veggieIngredientList = getVeggieIngredientAnalysisList(productResponse),
        veganStatus = getVeganStatus(productResponse),
        vegetarianStatus = getVegetarianStatus(productResponse),
        palmOilStatus = getPalmOilStatus(productResponse),
        servingQuantity = productResponse?.servingQuantity,
        unit = if (productResponse?.nutritionScoreBeverage == 1) "ml" else "g",
        nutrientsList = createNutrientsList(productResponse)
    )
}