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

import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.AdditiveResponse
import com.atharok.barcodescanner.domain.entity.dependencies.Additive
import com.atharok.barcodescanner.domain.entity.dependencies.AdditiveClass
import com.atharok.barcodescanner.domain.entity.dependencies.OverexposureRiskRate
import com.atharok.barcodescanner.domain.repositories.AdditiveClassRepository
import com.atharok.barcodescanner.domain.repositories.AdditivesRepository

class AdditivesRepositoryImpl(private val additiveResponseRepository: AdditiveResponseRepository,
                              private val additiveClassRepository: AdditiveClassRepository): AdditivesRepository {


    override suspend fun getAdditivesList(additiveFileNameWithExtension: String,
                                          additiveFileUrlName: String,
                                          tagList: List<String>,
                                          additiveClassFileNameWithExtension: String,
                                          additiveClassFileUrlName: String): List<Additive> {

        val additiveResponseList: List<AdditiveResponse> =
            additiveResponseRepository.getAdditiveResponseList(
                fileNameWithExtension = additiveFileNameWithExtension,
                fileUrlName = additiveFileUrlName,
                tagList = tagList
            )

        val additiveClassTagList: List<String> = generateAdditivesClassesTagsList(additiveResponseList)

        val additiveClassList: List<AdditiveClass> = additiveClassRepository.getAdditiveClassList(
            additiveClassFileNameWithExtension, additiveClassFileUrlName, additiveClassTagList)

        return generateAdditivesList(additiveResponseList, additiveClassList)
    }

    /**
     * Récupère tous les tags de tous les AdditiveClass de tous les AdditiveResponse.
     */
    private fun generateAdditivesClassesTagsList(additivesResponseList: List<AdditiveResponse>): List<String> {
        val allAdditivesClassesTagsList = mutableListOf<String>()
        for(additiveResponse in additivesResponseList){
            val additivesClassesTagsList = additiveResponse.additivesClasses?.value?.replace(", ", ",")?.split(",")
            if(additivesClassesTagsList!=null) {
                for (additiveClassTag in additivesClassesTagsList) {
                    if(!allAdditivesClassesTagsList.contains(additiveClassTag))
                        allAdditivesClassesTagsList.add(additiveClassTag)
                }
            }
        }
        return allAdditivesClassesTagsList
    }

    private fun generateAdditivesList(additivesResponseList: List<AdditiveResponse>,
                                      additiveClassList: List<AdditiveClass>): List<Additive> {

        val additivesList: MutableList<Additive> = mutableListOf()

        for(additiveResponse in additivesResponseList){

            // Si on n'a pas le nom de l'additif, on met celui du tag
            val additiveName: String = additiveResponse.name?.toLocaleLanguage() ?: additiveResponse.tag

            val thisAdditiveClassList = mutableListOf<AdditiveClass>()
            val additiveClassTagsList = additiveResponse.additivesClasses?.value?.replace(", ", ",")?.split(",")

            if(!additiveClassTagsList.isNullOrEmpty()) {
                // Recupère dans la liste des AdditiveClass, les AdditiveClass que possèdent l'AdditiveResponse
                for (additiveClass in additiveClassList) {
                    if (additiveClassTagsList.contains(additiveClass.tag)){
                        thisAdditiveClassList.add(additiveClass)
                    }
                }
            }

            additivesList.add(
                Additive(
                    tag = additiveResponse.tag,
                    name = additiveName,
                    additiveClassList = thisAdditiveClassList,
                    overexposureRiskRate = determineOverexposureRisk(additiveResponse)
                )
            )

        }

        return additivesList
    }

    private fun determineOverexposureRisk(additiveResponse: AdditiveResponse?): OverexposureRiskRate {

        return when(additiveResponse?.efsaEvaluationOverexposureRisk?.value){
            OverexposureRiskRate.LOW.id -> OverexposureRiskRate.LOW
            OverexposureRiskRate.MODERATE.id -> OverexposureRiskRate.MODERATE
            OverexposureRiskRate.HIGH.id -> OverexposureRiskRate.HIGH
            else -> OverexposureRiskRate.NONE
        }
    }
}