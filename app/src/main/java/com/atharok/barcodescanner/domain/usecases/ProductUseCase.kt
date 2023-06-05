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

package com.atharok.barcodescanner.domain.usecases

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.RemoteAPI
import com.atharok.barcodescanner.domain.repositories.BeautyProductRepository
import com.atharok.barcodescanner.domain.repositories.BookProductRepository
import com.atharok.barcodescanner.domain.repositories.FoodProductRepository
import com.atharok.barcodescanner.domain.repositories.PetFoodProductRepository
import com.atharok.barcodescanner.domain.resources.Resource
import kotlinx.coroutines.Dispatchers

class ProductUseCase(private val foodProductRepository: FoodProductRepository,
                     private val beautyProductRepository: BeautyProductRepository,
                     private val petFoodProductRepository: PetFoodProductRepository,
                     private val bookProductRepository: BookProductRepository) {

    fun getProduct(barcode: Barcode, apiRemote: RemoteAPI): LiveData<Resource<BarcodeAnalysis>> = liveData(Dispatchers.IO) {

        emit(Resource.loading())

        var barcodeAnalysis: BarcodeAnalysis? = null
        try {
            barcodeAnalysis = when(apiRemote) {
                RemoteAPI.OPEN_FOOD_FACTS -> foodProductRepository.getFoodProduct(barcode)
                RemoteAPI.OPEN_BEAUTY_FACTS -> beautyProductRepository.getBeautyProduct(barcode)
                RemoteAPI.OPEN_PET_FOOD_FACTS -> petFoodProductRepository.getPetFoodProduct(barcode)
                RemoteAPI.OPEN_LIBRARY -> bookProductRepository.getBookProduct(barcode)
                RemoteAPI.NONE -> null
            }

            if (barcodeAnalysis == null)
                barcodeAnalysis = DefaultBarcodeAnalysis(barcode, apiRemote)

            emit(Resource.success(barcodeAnalysis))

        } catch (e: Exception) {
            emit(Resource.failure(e, barcodeAnalysis))
        }
    }

    /*fun getProduct(barcode: Barcode): LiveData<Resource<BarcodeAnalysis>> = liveData(Dispatchers.IO) {

        emit(Resource.loading())

        var barcodeAnalysis: BarcodeAnalysis? = null
        try {
            barcodeAnalysis = when (barcode.getBarcodeType()) {
                BarcodeType.FOOD -> foodProductRepository.getFoodProduct(barcode)
                BarcodeType.BEAUTY -> beautyProductRepository.getBeautyProduct(barcode)
                BarcodeType.PET_FOOD -> petFoodProductRepository.getPetFoodProduct(barcode)
                BarcodeType.BOOK -> bookProductRepository.getBookProduct(barcode)

                else -> {
                    when {
                        barcode.isBookBarcode() -> bookProductRepository.getBookProduct(barcode)
                        //else -> searchEverywhere(barcode)
                    }
                }
            }

            if (barcodeAnalysis == null)
                barcodeAnalysis = DefaultBarcodeAnalysis(barcode)

            emit(Resource.success(barcodeAnalysis))

        } catch (e: Exception) {
            emit(Resource.failure(e, barcodeAnalysis))
        }
    }*/

    /*private suspend fun searchEverywhere(barcode: Barcode): FoodBarcodeAnalysis? {

        var product: FoodBarcodeAnalysis? = null
        for(i in 0..2){

            product = when(i){
                0 -> foodProductRepository.getFoodProduct(barcode)
                1 -> beautyProductRepository.getBeautyProduct(barcode)
                else -> petFoodProductRepository.getPetFoodProduct(barcode)
            }

            if(product != null){
                break
            }
        }

        return product
    }*/

    fun refresh(){
        Resource.loading(null)
    }
}