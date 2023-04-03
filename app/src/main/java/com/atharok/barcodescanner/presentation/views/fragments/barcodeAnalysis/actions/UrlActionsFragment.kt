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
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.URIParsedResult

class UrlActionsFragment: AbstractActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(parsedResult){
            is URIParsedResult -> configureUrlActions(barcode)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configureUrlActions(barcode: Barcode) = arrayOf(
        ActionItem(R.string.action_open_link, R.drawable.baseline_open_in_browser_24, openUrl(barcode.contents)),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(barcode.contents)),
        ActionItem(R.string.copy_label, R.drawable.baseline_content_copy_24, copyContents(barcode.contents))
    )
}