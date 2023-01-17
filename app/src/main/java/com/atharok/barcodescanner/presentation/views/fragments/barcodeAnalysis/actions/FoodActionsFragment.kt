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

import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult

class FoodActionsFragment: AbstractActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(barcode.getBarcodeType()){
            BarcodeType.FOOD -> configureFoodActions(barcode.contents)
            else -> configureDefaultActions(barcode.contents)
        }
    }

    private fun configureFoodActions(contents: String) = arrayOf(
        ActionItem(R.string.action_web_search_label, R.drawable.baseline_search_24, showUrlsAlertDialog(contents)),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(contents)),
        ActionItem(R.string.copy_label, R.drawable.baseline_content_copy_24, copyContents(contents))
    )

    // Actions

    private fun showUrlsAlertDialog(contents: String): () -> Unit = {
        val webUrl = getSearchEngineUrl(contents)
        val openFoodFactsUrl = getString(R.string.search_engine_open_food_facts_product_url, contents)

        val items = arrayOf<Pair<String, () -> Unit>>(
            Pair(getString(R.string.action_web_search_label), openUrl(webUrl)),
            Pair(getString(R.string.action_product_search_label, getString(R.string.open_food_facts_label)), openUrl(openFoodFactsUrl))
        )

        createAlertDialog(requireContext(), getString(R.string.search_label), items).show()
    }
}