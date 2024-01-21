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

import androidx.lifecycle.MutableLiveData
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPI
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPIError
import com.atharok.barcodescanner.domain.entity.analysis.UnknownProductBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.repositories.BeautyProductRepository
import com.atharok.barcodescanner.domain.repositories.BookProductRepository
import com.atharok.barcodescanner.domain.repositories.FoodProductRepository
import com.atharok.barcodescanner.domain.repositories.MusicProductRepository
import com.atharok.barcodescanner.domain.repositories.PetFoodProductRepository
import com.atharok.barcodescanner.domain.resources.Resource

class ProductUseCase(private val foodProductRepository: FoodProductRepository,
                     private val beautyProductRepository: BeautyProductRepository,
                     private val petFoodProductRepository: PetFoodProductRepository,
                     private val musicProductRepository: MusicProductRepository,
                     private val bookProductRepository: BookProductRepository) {

    val productObserver = MutableLiveData<Resource<BarcodeAnalysis>>()

    suspend fun fetchProduct(barcode: Barcode, remoteAPI: RemoteAPI) {
        productObserver.postValue(Resource.loading())

        try {
            val barcodeAnalysis: BarcodeAnalysis = when(remoteAPI) {
                RemoteAPI.OPEN_FOOD_FACTS -> foodProductRepository.getFoodProduct(barcode) ?: UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.NO_RESULT, source = remoteAPI)
                RemoteAPI.OPEN_BEAUTY_FACTS -> beautyProductRepository.getBeautyProduct(barcode) ?: UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.NO_RESULT, source = remoteAPI)
                RemoteAPI.OPEN_PET_FOOD_FACTS -> petFoodProductRepository.getPetFoodProduct(barcode) ?: UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.NO_RESULT, source = remoteAPI)
                RemoteAPI.MUSICBRAINZ -> musicProductRepository.getMusicProduct(barcode) ?: UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.NO_RESULT, source = remoteAPI)
                RemoteAPI.OPEN_LIBRARY -> bookProductRepository.getBookProduct(barcode) ?: UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.NO_RESULT, source = remoteAPI)
                RemoteAPI.NONE -> UnknownProductBarcodeAnalysis(
                    barcode = barcode,
                    apiError = RemoteAPIError.AUTOMATIC_API_RESEARCH_DISABLED,
                    source = when(barcode.getBarcodeType()) {
                        BarcodeType.FOOD -> RemoteAPI.OPEN_FOOD_FACTS
                        BarcodeType.PET_FOOD -> RemoteAPI.OPEN_PET_FOOD_FACTS
                        BarcodeType.BEAUTY -> RemoteAPI.OPEN_BEAUTY_FACTS
                        BarcodeType.MUSIC -> RemoteAPI.MUSICBRAINZ
                        BarcodeType.BOOK -> RemoteAPI.OPEN_LIBRARY
                        else -> remoteAPI
                    }
                )
            }

            productObserver.postValue(Resource.success(barcodeAnalysis))

        } catch (e: Exception) {
            productObserver.postValue(Resource.failure(e, UnknownProductBarcodeAnalysis(barcode, RemoteAPIError.ERROR, e.toString(), remoteAPI)))
            e.printStackTrace()
        }
    }
}