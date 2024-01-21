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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions

import android.view.View
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem

class PetFoodActionsFragment: AbstractActionsFragment() {
    override fun configureActions(barcode: Barcode): Array<ActionItem> {
        return when(barcode.getBarcodeType()){
            BarcodeType.PET_FOOD -> configurePetFoodActions(barcode)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configurePetFoodActions(barcode: Barcode) = arrayOf(
        ActionItem(R.string.action_web_search_label, R.drawable.baseline_search_24, showUrlsAlertDialog(barcode.contents)),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(barcode.contents)),
        ActionItem(R.string.copy_barcode_label, R.drawable.baseline_content_copy_24, copyContents(barcode.contents)),
        ActionItem(R.string.action_modify_barcode, R.drawable.baseline_create_24, modifyBarcodeContents(barcode))
    )

    // Actions

    private fun showUrlsAlertDialog(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val openPetFoodFactsUrl = getString(R.string.search_engine_open_pet_food_facts_product_url, contents)

            val items = arrayOf<Pair<String, ActionItem.OnActionItemListener>>(
                Pair(getString(R.string.action_web_search_label), openContentsWithSearchEngine(contents)),
                Pair(getString(R.string.action_product_search_label, getString(R.string.open_pet_food_facts_label)), openUrl(openPetFoodFactsUrl))
            )

            createAlertDialog(requireContext(), getString(R.string.search_label), items).show()
        }
    }
}