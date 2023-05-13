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

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.atharok.barcodescanner.common.utils.ADDITIVES_CLASSES_LOCALE_FILE_NAME
import com.atharok.barcodescanner.common.utils.ADDITIVES_CLASSES_URL
import com.atharok.barcodescanner.common.utils.ADDITIVES_LOCALE_FILE_NAME
import com.atharok.barcodescanner.common.utils.ADDITIVES_URL
import com.atharok.barcodescanner.common.utils.ALLERGENS_LOCALE_FILE_NAME
import com.atharok.barcodescanner.common.utils.ALLERGENS_URL
import com.atharok.barcodescanner.common.utils.COUNTRIES_LOCALE_FILE_NAME
import com.atharok.barcodescanner.common.utils.COUNTRIES_URL
import com.atharok.barcodescanner.common.utils.LABELS_LOCALE_FILE_NAME
import com.atharok.barcodescanner.common.utils.LABELS_URL
import com.atharok.barcodescanner.domain.entity.dependencies.Additive
import com.atharok.barcodescanner.domain.entity.dependencies.Allergen
import com.atharok.barcodescanner.domain.entity.dependencies.Country
import com.atharok.barcodescanner.domain.entity.dependencies.Label
import com.atharok.barcodescanner.domain.usecases.ExternalFoodProductDependencyUseCase

/**
 * Certaines données récupérées sur OpenFoodFacts / OpenBeautyFacts / OpenPetFoodFacts contiennent
 * des tags permettant de récupérer des informations supplémentaires dans d'autres fichiers
 * externes. Ce ViewModel permet de récupérer ces données dans ces fichiers externes.
 */
class ExternalFileViewModel(
    private val externalFoodProductDependencyUseCase: ExternalFoodProductDependencyUseCase
    ): ViewModel() {

    fun obtainLabelsList(tagList: List<String>): LiveData<List<Label>> {

        return externalFoodProductDependencyUseCase.obtainLabelsList(
            fileNameWithExtension = LABELS_LOCALE_FILE_NAME,
            fileUrlName = LABELS_URL,
            tagList = tagList
        )
    }

    fun obtainAdditivesList(tagList: List<String>): LiveData<List<Additive>> {
        return externalFoodProductDependencyUseCase.obtainAdditivesList(
            additiveFileNameWithExtension = ADDITIVES_LOCALE_FILE_NAME,
            additiveFileUrlName = ADDITIVES_URL,
            tagList = tagList,
            additiveClassFileNameWithExtension = ADDITIVES_CLASSES_LOCALE_FILE_NAME,
            additiveClassFileUrlName = ADDITIVES_CLASSES_URL
        )
    }

    fun obtainAllergensList(tagList: List<String>): LiveData<List<Allergen>> {

        return externalFoodProductDependencyUseCase.obtainAllergensList(
            fileNameWithExtension = ALLERGENS_LOCALE_FILE_NAME,
            fileUrlName = ALLERGENS_URL,
            tagList = tagList
        )
    }

    fun obtainCountriesList(tagList: List<String>): LiveData<List<Country>> {

        return externalFoodProductDependencyUseCase.obtainCountriesList(
            fileNameWithExtension = COUNTRIES_LOCALE_FILE_NAME,
            fileUrlName = COUNTRIES_URL,
            tagList = tagList
        )
    }
}