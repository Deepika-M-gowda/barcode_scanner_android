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
import com.google.zxing.client.result.GeoParsedResult
import com.google.zxing.client.result.ParsedResult

class LocalizationActionsFragment: AbstractParsedResultActionsFragment() {
    override fun configureActionItems(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult is GeoParsedResult) {
            addActionItem(configureLocalizationActionItem(barcode))
        }
        addActionItem(configureSearchOnWebActionItem(barcode))
        addActionItem(configureShareTextActionItem(barcode))
        addActionItem(configureCopyTextActionItem(barcode))
        addActionItem(configureModifyBarcodeActionItem(barcode))
    }

    private fun configureLocalizationActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.action_show_location,
            imageRes = R.drawable.baseline_place_24,
            listener = openUrl(barcode.contents)
        )
    }
}