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
import com.atharok.barcodescanner.domain.entity.dependencies.Additive
import com.atharok.barcodescanner.domain.entity.dependencies.Allergen
import com.atharok.barcodescanner.domain.entity.dependencies.Country
import com.atharok.barcodescanner.domain.entity.dependencies.Label
import com.atharok.barcodescanner.domain.repositories.AdditivesRepository
import com.atharok.barcodescanner.domain.repositories.AllergensRepository
import com.atharok.barcodescanner.domain.repositories.CountriesRepository
import com.atharok.barcodescanner.domain.repositories.LabelsRepository
import kotlinx.coroutines.Dispatchers

class ExternalFoodProductDependencyUseCase(private val labelsRepository: LabelsRepository,
                                           private val additivesRepository: AdditivesRepository,
                                           private val allergensRepository: AllergensRepository,
                                           private val countriesRepository: CountriesRepository) {

    fun obtainLabelsList(
        fileNameWithExtension: String,
        fileUrlName: String,
        tagList: List<String>): LiveData<List<Label>> = liveData(Dispatchers.IO) {

        emit(labelsRepository.getLabels(fileNameWithExtension, fileUrlName, tagList))
    }

    fun obtainAdditivesList(
        additiveFileNameWithExtension: String,
        additiveFileUrlName: String,
        tagList: List<String>,
        additiveClassFileNameWithExtension: String,
        additiveClassFileUrlName: String): LiveData<List<Additive>> = liveData(
        Dispatchers.IO) {

        emit(
            additivesRepository.getAdditivesList(
                additiveFileNameWithExtension = additiveFileNameWithExtension,
                additiveFileUrlName = additiveFileUrlName,
                tagList = tagList,
                additiveClassFileNameWithExtension = additiveClassFileNameWithExtension,
                additiveClassFileUrlName = additiveClassFileUrlName)
        )
    }

    fun obtainAllergensList(
        fileNameWithExtension: String,
        fileUrlName: String,
        tagList: List<String>): LiveData<List<Allergen>> = liveData(Dispatchers.IO) {

        emit(allergensRepository.getAllergensList(fileNameWithExtension, fileUrlName, tagList))
    }

    fun obtainCountriesList(
        fileNameWithExtension: String,
        fileUrlName: String,
        tagList: List<String>): LiveData<List<Country>> = liveData(Dispatchers.IO) {

        emit(countriesRepository.getCountriesList(fileNameWithExtension, fileUrlName, tagList))
    }
}