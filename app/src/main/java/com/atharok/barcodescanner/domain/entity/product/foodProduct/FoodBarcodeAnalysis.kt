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

package com.atharok.barcodescanner.domain.entity.product.foodProduct

import androidx.annotation.Keep
import com.atharok.barcodescanner.domain.entity.product.ApiSource
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis

@Keep
class FoodBarcodeAnalysis(
    override val barcode: Barcode,
    override val source: ApiSource,
    val name: String?,
    val brands: String?,
    val quantity: String?,
    val imageFrontUrl: String?,
    val labels: String?,
    val labelsTagList: List<String>?,
    val categories: String?,
    val packaging: String?,
    val stores: String?,
    val salesCountriesTagsList: List<String>?,
    val originsCountriesTagsList: List<String>?,
    val nutriscore: Nutriscore,
    val novaGroup: NovaGroup,
    val ecoScore: EcoScore,
    val ingredients: String?,
    val tracesTagsList: List<String>?,
    val allergensTagsList: List<String>?,
    val additivesTagsList: List<String>?,
    val veggieIngredientList: List<VeggieIngredientAnalysis>?,
    val veganStatus: VeganStatus,
    val vegetarianStatus: VegetarianStatus,
    val palmOilStatus: PalmOilStatus,
    val servingQuantity: Double?,
    val unit: String,
    val nutrientsList: List<Nutrient>): BarcodeAnalysis(barcode, source) {

    val contains100gValues: Boolean = nutrientsList.any { it.values.value100g != null }

    val containsServingValues: Boolean = nutrientsList.any { it.values.valueServing != null }

    /**
     * Vérifie si on possède un Nutrient de type FAT ou SATURATED_FAT ou SUGARS ou SALT
     */
    val containsNutrientLevel: Boolean = nutrientsList.any {
        it.entitled == NutritionFactsEnum.FAT ||
        it.entitled == NutritionFactsEnum.SATURATED_FAT ||
        it.entitled == NutritionFactsEnum.SUGARS ||
        it.entitled == NutritionFactsEnum.SALT
    }

    // On met les tags des allergens et des traces ensemble car les 2 sont trouvables dans le même fichier (allergens.json)
    val allergensAndTracesTagList: List<String>? = when {
        allergensTagsList != null && tracesTagsList != null -> allergensTagsList + tracesTagsList
        allergensTagsList != null -> allergensTagsList
        tracesTagsList != null -> tracesTagsList
        else -> null
    }?.distinct()?.toList()

    val countriesTagList: List<String>? = when {
        salesCountriesTagsList != null && originsCountriesTagsList != null -> salesCountriesTagsList + originsCountriesTagsList
        salesCountriesTagsList != null -> salesCountriesTagsList
        originsCountriesTagsList != null -> originsCountriesTagsList
        else -> null
    }?.distinct()?.toList()
}