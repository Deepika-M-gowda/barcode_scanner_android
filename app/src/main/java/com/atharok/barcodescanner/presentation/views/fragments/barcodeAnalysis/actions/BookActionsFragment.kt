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
import com.google.zxing.client.result.ParsedResult

class BookActionsFragment: AbstractActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(barcode.getBarcodeType()){
            BarcodeType.BOOK -> configureBookActions(barcode)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configureBookActions(barcode: Barcode) = arrayOf(
        ActionItem(R.string.action_web_search_label, R.drawable.baseline_search_24, showUrlsAlertDialog(barcode.contents)),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(barcode.contents)),
        ActionItem(R.string.copy_label, R.drawable.baseline_content_copy_24, copyContents(barcode.contents))
    )

    // Actions

    private fun showUrlsAlertDialog(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val amazonUrl = getString(R.string.search_engine_amazon_url, contents)
            val ebayUrl = getString(R.string.search_engine_ebay_url, contents)
            val fnacUrl = getString(R.string.search_engine_fnac_url, contents)
            val openLibraryUrl = getString(R.string.search_engine_open_library_product_url, contents)

            val items = arrayOf<Pair<String, ActionItem.OnActionItemListener>>(
                Pair(getString(R.string.action_web_search_label), openContentsWithSearchEngine(contents)),
                Pair(getString(R.string.action_product_search_label, getString(R.string.amazon_label)), openUrl(amazonUrl)),
                Pair(getString(R.string.action_product_search_label, getString(R.string.ebay_label)), openUrl(ebayUrl)),
                Pair(getString(R.string.action_product_search_label, getString(R.string.fnac_label)), openUrl(fnacUrl)),
                Pair(getString(R.string.action_product_search_label, getString(R.string.open_library_label)), openUrl(openLibraryUrl))
            )

            createAlertDialog(requireContext(), getString(R.string.search_label), items).show()
        }
    }
}