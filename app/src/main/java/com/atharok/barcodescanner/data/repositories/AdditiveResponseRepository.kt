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
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.file.JsonManager
import java.io.File

class AdditiveResponseRepository(private val fileFetcher: FileFetcher) {

    fun getAdditiveResponseList(fileNameWithExtension: String,
                                        fileUrlName: String,
                                        tagList: List<String>): List<AdditiveResponse> {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getAdditiveResponseList(tagList, file) else listOf()
    }

    private fun getAdditiveResponseList(tagList: List<String>, jsonFile: File): List<AdditiveResponse> {

        val additiveResponseList = mutableListOf<AdditiveResponse>()
        val jsonManager = JsonManager<AdditiveResponse>(jsonFile)

        for (tag in tagList) {
            // On récupère un AllergenResponse dans le fichier Json correspondant au tag
            val additiveResponse: AdditiveResponse? = jsonManager.get(tag, AdditiveResponse::class)

            if(additiveResponse != null) {
                additiveResponse.tag = tag
                additiveResponseList.add(additiveResponse)
            }
        }

        return additiveResponseList
    }
}