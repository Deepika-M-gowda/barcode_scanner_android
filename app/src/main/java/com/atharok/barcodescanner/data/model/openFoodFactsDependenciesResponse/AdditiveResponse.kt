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

package com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse

import androidx.annotation.Keep
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.commons.EnValue
import com.atharok.barcodescanner.data.model.openFoodFactsDependenciesResponse.commons.LanguageValue
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Cette classe est une valeur de la liste "additives.json" convertit en Kotlin. Le
 * fichier est récupéré via l'URL "https://world.openfoodfacts.org/data/taxonomies/additives.json".
 */
@Keep
data class AdditiveResponse(
    @SerializedName("vegan")
    @Expose
    val vegan: EnValue? = null,

    @SerializedName("vegetarian")
    @Expose
    val vegetarian: EnValue? = null,

    @SerializedName("efsa_evaluation")
    @Expose
    val efsaEvaluation: EnValue? = null,

    @SerializedName("additives_classes")
    @Expose
    val additivesClasses: EnValue? = null,

    @SerializedName("efsa_evaluation_overexposure_risk")
    @Expose
    val efsaEvaluationOverexposureRisk: EnValue? = null,

    @SerializedName("organic_eu")
    @Expose
    val organicEu: EnValue? = null,

    @SerializedName("e_number")
    @Expose
    val eNumber: EnValue? = null,

    @SerializedName("wikidata")
    @Expose
    val wikidata: EnValue? = null,

    @SerializedName("efsa_evaluation_date")
    @Expose
    val efsaEvaluationDate: EnValue? = null,

    @SerializedName("efsa_evaluation_url")
    @Expose
    val efsaEvaluationUrl: EnValue? = null,

    @SerializedName("name")
    @Expose
    val name: LanguageValue? = null,

    @SerializedName("efsa_evaluation_exposure_95th_greater_than_adi")
    @Expose
    val efsaEvaluationExposure95thGreaterThanAdi: EnValue? = null,

    @SerializedName("efsa_evaluation_exposure_mean_greater_than_adi")
    @Expose
    val efsaEvaluationExposureMeanGreaterThanAdi: EnValue? = null
) {
    var tag: String = ""
}