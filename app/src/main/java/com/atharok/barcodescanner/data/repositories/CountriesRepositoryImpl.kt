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

import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.file.JsonManager
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.CountryResponse
import com.atharok.barcodescanner.domain.entity.dependencies.Country
import com.atharok.barcodescanner.domain.repositories.CountriesRepository
import java.io.File

class CountriesRepositoryImpl(private val fileFetcher: FileFetcher): CountriesRepository {

    override suspend fun getCountriesList(fileNameWithExtension: String,
                                          fileUrlName: String,
                                          tagList: List<String>): List<Country> {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getCountriesList(tagList, file) else listOf()
    }

    override suspend fun getCountries(fileNameWithExtension: String,
                                      fileUrlName: String,
                                      tagList: List<String>): String {

        val file = fileFetcher.fetchFile(fileNameWithExtension, fileUrlName)

        return if(file.exists()) getCountries(tagList, file) else ""
    }

    /**
     * Récupère une liste de Country.
     */
    private fun getCountriesList(tagList: List<String>, jsonFile: File): List<Country> {

        val countryList = mutableListOf<Country>()
        val jsonManager = JsonManager<CountryResponse>(jsonFile)

        for (tag in tagList) {
            // On récupère un CountryResponse dans le fichier Json correspondant au tag
            val countryResponse: CountryResponse? = jsonManager.get(tag, CountryResponse::class)

            // Si on n'a pas le nom du pays, on met celui du tag
            val countryName = countryResponse?.name?.toLocaleLanguage() ?: tag

            if(countryName.isNotBlank()){
                // On génère un Country à partir de CountryResponse qu'on ajoute à la liste
                countryList.add(Country(tag = tag, name = countryName.trim()))
            }
        }

        return countryList
    }

    /**
     * Récupère une liste de Country au format String.
     */
    private fun getCountries(tagList: List<String>, jsonFile: File): String {

        val strBuilder = StringBuilder()
        val jsonManager = JsonManager<CountryResponse>(jsonFile)

        for (tag in tagList) {

            // On récupère un CountryResponse dans le fichier Json correspondant au tag
            val countryResponse: CountryResponse? = jsonManager.get(tag, CountryResponse::class)

            // Si on n'a pas le nom du pays, on met celui du tag
            val countryName = countryResponse?.name?.toLocaleLanguage() ?: tag

            if(countryName.isNotBlank()){
                strBuilder.append(countryName.trim())

                if (tagList.last() != tag)
                    strBuilder.append(", ")
            }
        }

        return strBuilder.toString()
    }
}