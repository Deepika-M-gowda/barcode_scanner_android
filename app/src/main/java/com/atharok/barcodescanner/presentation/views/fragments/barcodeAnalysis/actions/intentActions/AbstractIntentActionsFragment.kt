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

import android.content.ActivityNotFoundException
import android.content.Intent
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment
import com.google.zxing.client.result.ParsedResult
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractIntentActionsFragment: ActionsFragment() {

    protected fun addIntentActionFAB(action: ActionEnum, parsedResult: ParsedResult){
        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(action.drawableResource) {
            try {
                val intent = myScope.get<Intent>(named(action)) {
                    parametersOf(parsedResult)
                }
                startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                showToastText(R.string.barcode_search_error_label)
            }
        }
    }
}