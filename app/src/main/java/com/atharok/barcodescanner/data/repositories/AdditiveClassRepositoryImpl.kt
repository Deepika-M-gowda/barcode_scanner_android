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

import android.content.Context
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.AdditiveClassResponse
import com.atharok.barcodescanner.domain.entity.dependencies.AdditiveClass
import com.atharok.barcodescanner.domain.repositories.AdditiveClassRepository
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.file.JsonManager
import java.io.File

class AdditiveClassRepositoryImpl(private val context: Context,
                                  private val fileFetcher: FileFetcher): AdditiveClassRepository {


    override suspend fun getAdditiveClassList(fileNameWithExtension: String,
                                              fileUrlName: String,
                                              tagList: List<String>): List<AdditiveClass> {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getAdditiveClassList(tagList, file) else listOf()
    }

    /**
     * Récupère une liste d'AdditiveClass.
     */
    private fun getAdditiveClassList(tagList: List<String>, jsonFile: File): List<AdditiveClass> {

        val additiveClassList = mutableListOf<AdditiveClass>()
        val jsonManager = JsonManager<AdditiveClassResponse>(jsonFile)

        for (tag in tagList) {
            // On récupère un AdditiveClassResponse dans le fichier Json correspondant au tag
            val additiveClassResponse: AdditiveClassResponse? = jsonManager.get(tag, AdditiveClassResponse::class)

            // Si on n'a pas le nom de l'AdditiveClass, on met celui du tag
            val additiveClassName = additiveClassResponse?.name?.toLocaleLanguage() ?: tag

            if(additiveClassName.isNotBlank()) {
                // On génère un AdditiveClass à partir de l'AdditiveClassResponse qu'on ajoute à la liste
                additiveClassList.add(
                    AdditiveClass(
                        tag = tag,
                        name = additiveClassName.trim(),
                        description = getDescription(tag, additiveClassResponse).trim()
                    )
                )
            }
        }

        return additiveClassList
    }

    private fun getDescription(tag: String, additiveClassResponse: AdditiveClassResponse?): String {

        val description = additiveClassResponse?.description?.toLocaleLanguage()

        return if(description.isNullOrBlank()){

            // Non présent dans fichier: additives_classes.json
            if(tag == "en:stabilizer"){
                context.getString(R.string.stabilizer_description_label)
            }else{
                context.getString(R.string.additive_no_information_found_label)
            }
        } else  {
            description
        }
    }
}