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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogSearchActions

import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import org.koin.android.ext.android.get

class PetFoodActionsFragment: AbstractSearchDialogIntentActionsFragment() {

    override fun start(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult.type == ParsedResultType.PRODUCT && barcode.getBarcodeType() == BarcodeType.PET_FOOD) {
            configurePetFoodProductSearchFAB(barcode.contents)
        }
    }

    private fun configurePetFoodProductSearchFAB(contents: String){

        val searchLabelArray = arrayOf(
            getString(R.string.action_web_search_label),
            getString(R.string.action_product_search_label, getString(R.string.open_pet_food_facts_label)))

        val searchUrlArray = arrayOf(
            get<SettingsManager>().getSearchEngineUrl(contents),
            getString(R.string.search_engine_open_pet_food_facts_product_url, contents)
        )

        addSearchDialogActionFAB(searchLabelArray, searchUrlArray)
    }
}