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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.intentActions

import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType

class UrlActionsFragment: AbstractIntentActionsFragment() {

    override fun start(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult.type == ParsedResultType.URI) {
             addIntentActionFAB(ActionEnum.SEARCH_URL, parsedResult)
        }
    }
}