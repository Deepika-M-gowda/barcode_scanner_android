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

import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.AllergenResponse
import com.atharok.barcodescanner.domain.entity.dependencies.Allergen
import com.atharok.barcodescanner.domain.repositories.AllergensRepository
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.file.JsonManager
import java.io.File

class AllergensRepositoryImpl(private val fileFetcher: FileFetcher): AllergensRepository {

    override suspend fun getAllergensList(fileNameWithExtension: String,
                                          fileUrlName: String,
                                          tagList: List<String>): List<Allergen> {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getAllergensList(tagList, file) else listOf()
    }

    override suspend fun getAllergens(fileNameWithExtension: String,
                                      fileUrlName: String,
                                      tagList: List<String>): String {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getAllergens(tagList, file) else ""
    }

    /**
     * Récupère une liste d'Allergen.
     */
    private fun getAllergensList(tagList: List<String>, jsonFile: File): List<Allergen> {

        val allergensList = mutableListOf<Allergen>()
        val jsonManager = JsonManager<AllergenResponse>(jsonFile)

        for (tag in tagList) {
            // On récupère un AllergenResponse dans le fichier Json correspondant au tag
            val allergenResponse: AllergenResponse? = jsonManager.get(tag, AllergenResponse::class)

            // Si on n'a pas le nom de l'allergènes, on met celui du tag en retirant le préfix "fr:"
            val allergenName = allergenResponse?.name?.toLocaleLanguage() ?: tag.removePrefix("fr:")

            if(allergenName.isNotBlank()) {
                // On génère un Allergen à partir de l'AllergenResponse qu'on ajoute à la liste
                allergensList.add(Allergen(tag = tag, name = allergenName.trim()))
            }
        }

        return allergensList
    }

    /**
     * Récupère une liste d'Allergen au format String.
     */
    private fun getAllergens(tagList: List<String>, jsonFile: File): String {

        val strBuilder = StringBuilder()
        val jsonManager = JsonManager<AllergenResponse>(jsonFile)

        for (tag in tagList) {

            // On récupère un AllergenResponse dans le fichier Json correspondant au tag
            val allergenResponse: AllergenResponse? = jsonManager.get(tag, AllergenResponse::class)

            // Si on n'a pas le nom de l'allergènes, on met celui du tag en retirant le préfix "fr:"
            val allergenName = allergenResponse?.name?.toLocaleLanguage() ?: tag.removePrefix("fr:")

            if(allergenName.isNotBlank()) {
                strBuilder.append(allergenName.trim())

                if (tagList.last() != tag)
                    strBuilder.append(", ")
            }
        }

        return strBuilder.toString()
    }
}