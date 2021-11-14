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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogIntentActions

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment
import com.google.zxing.client.result.ParsedResult
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractDialogIntentActionsFragment: ActionsFragment() {

    protected fun addDialogActionFAB(action: ActionEnum, actionsArray: Array<ActionEnum>, parsedResult: ParsedResult) {
        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(action.drawableResource) {
            val alertDialog = createAlertDialog(action, actionsArray, parsedResult)
            alertDialog.show()
        }
    }

    private fun createAlertDialog(action: ActionEnum, actionsArray: Array<ActionEnum>, parsedResult: ParsedResult): AlertDialog {
        val onClickListener = DialogInterface.OnClickListener { _, i ->
            val intent: Intent = myScope.get(named(actionsArray[i])) { parametersOf(parsedResult) }
            startActivity(intent)
        }

        val itemLabelList = mutableListOf<String>()
        actionsArray.forEach {
            itemLabelList.add(getString(it.stringResource))
        }

        return AlertDialog.Builder(requireActivity()).apply {
            setTitle(action.stringResource)
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
            setItems(itemLabelList.toTypedArray(), onClickListener)
        }.create()
    }
}