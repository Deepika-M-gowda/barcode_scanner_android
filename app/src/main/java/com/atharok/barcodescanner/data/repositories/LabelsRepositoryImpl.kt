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

import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.LabelResponse
import com.atharok.barcodescanner.domain.entity.dependencies.Label
import com.atharok.barcodescanner.domain.repositories.LabelsRepository
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.file.JsonManager
import java.io.File

class LabelsRepositoryImpl(private val fileFetcher: FileFetcher): LabelsRepository {

    override suspend fun getLabels(
        fileNameWithExtension: String,
        fileUrlName: String,
        tagList: List<String>
    ): List<Label> {
        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) {
            obtainList(file, tagList)
        } else listOf()
    }

    /**
     * Récupère une liste de Label.
     */
    private fun obtainList(jsonFile: File, tagList: List<String>): List<Label> {

        val labels = mutableListOf<Label>()

        val jsonManager = JsonManager<Array<LabelResponse>>(jsonFile)
        val jsonLabelResponses: Array<LabelResponse>? = getFromJson(jsonManager)

        if(!jsonLabelResponses.isNullOrEmpty()) {

            for(tag in tagList){
                for(schema in jsonLabelResponses){
                    if(tag == schema.id){
                        labels.add(Label(tag = tag, imageUrl = schema.image))
                        break
                    }
                }
            }
        }

        return labels
    }

    /**
     * La clé "tags" est le nom du tableau que l'on récupère dans le fichier Json.
     */
    private inline fun <reified T: Any> getFromJson(jsonManager: JsonManager<T>) = jsonManager.get("tags", T::class)
}