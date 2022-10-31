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

package com.atharok.barcodescanner.data.repositories

import com.atharok.barcodescanner.domain.entity.product.ApiSource
import com.atharok.barcodescanner.data.api.OpenPetFoodFactsService
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.repositories.PetFoodProductRepository

class PetFoodProductRepositoryImpl(private val service: OpenPetFoodFactsService): PetFoodProductRepository {
    override suspend fun getPetFoodProduct(barcode: Barcode): FoodBarcodeAnalysis? {
        val petFoodProductResponse =  service.getOpenPetFoodFactsData(barcode.contents)

        if(petFoodProductResponse.status == 0 || petFoodProductResponse.productResponse == null)
            return null

        return petFoodProductResponse.toModel(barcode, ApiSource.OPEN_PET_FOOD_FACTS)
    }
}