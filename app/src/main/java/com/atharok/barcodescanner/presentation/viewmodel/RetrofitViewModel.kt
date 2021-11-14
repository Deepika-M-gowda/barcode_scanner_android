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

package com.atharok.barcodescanner.presentation.viewmodel
/*
import androidx.lifecycle.*
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.models.network.api.pojos.ApiResponse
import com.atharok.barcodescanner.models.network.api.repositories.OpenBeautyFactsRepository
import com.atharok.barcodescanner.models.network.api.repositories.OpenFoodFactsRepository
import com.atharok.barcodescanner.models.network.api.repositories.OpenLibraryRepository
import com.atharok.barcodescanner.models.network.api.repositories.OpenPetFoodFactsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class RetrofitViewModel(private val openFoodFactsRepository: OpenFoodFactsRepository,
                        private val openPetFoodFactsRepository: OpenPetFoodFactsRepository,
                        private val openBeautyFactsRepository: OpenBeautyFactsRepository,
                        private val openLibraryRepository: OpenLibraryRepository
): ViewModel() {

    private val responseLiveData = MutableLiveData<ApiResponse>()

    fun getAPIResponseFromWebService(barcode: Barcode): LiveData<out ApiResponse> {
        refresh(barcode)
        return responseLiveData
    }

    fun refresh(barcode: Barcode) = viewModelScope.launch {

        val deferred = async(Dispatchers.IO) {
            searchAPIResponseFromWebService(barcode)
        }

        responseLiveData.value = deferred.await()
    }

    /*fun getAPIResponseFromWebService(barcodeInformation: BarcodeInformation): LiveData<out ApiResponse> = liveData(Dispatchers.IO) {
        emit(searchAPIResponseFromWebService(barcodeInformation))
    }*/

    private suspend fun searchAPIResponseFromWebService(barcode: Barcode): ApiResponse {

        return when(barcode.getBarcodeType()){
            BarcodeType.BOOK -> openLibraryRepository.searchOnOpenLibrary(barcode.contents)
            BarcodeType.FOOD -> openFoodFactsRepository.searchOnOpenFoodFacts(barcode.contents)
            BarcodeType.BEAUTY -> openBeautyFactsRepository.searchOnOpenBeautyFacts(barcode.contents)
            BarcodeType.PET_FOOD -> openPetFoodFactsRepository.searchOnOpenPetFoodFacts(barcode.contents)

            else -> {
                if(barcode.isBookBarcode()) {
                    openLibraryRepository.searchOnOpenLibrary(barcode.contents)
                } else {
                    searchEverywhere(barcode.contents) // -> Cherche dans toutes les APIs (sauf OpenLibrary)
                }
            }
        }
    }

    private suspend fun searchEverywhere(barCodeContent: String): ApiResponse {

        lateinit var response: ApiResponse
        for(i in 0..2){

            response = when(i){
                0 -> openFoodFactsRepository.searchOnOpenFoodFacts(barCodeContent)
                1 -> openBeautyFactsRepository.searchOnOpenBeautyFacts(barCodeContent)
                else -> openPetFoodFactsRepository.searchOnOpenPetFoodFacts(barCodeContent)
            }

            if(response.status == 1){
                break
            }
        }

        return response
    }
}*/