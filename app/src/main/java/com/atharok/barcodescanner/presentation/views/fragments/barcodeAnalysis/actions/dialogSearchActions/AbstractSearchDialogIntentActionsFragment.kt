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

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AlertDialog
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment

abstract class AbstractSearchDialogIntentActionsFragment: ActionsFragment() {

    protected fun addSearchDialogActionFAB(searchLabelArray: Array<String>, searchUrlArray: Array<String>){
        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(ActionEnum.OPEN_SEARCH_DIALOG.drawableResource) {

            val alertDialog = createAlertDialog(searchLabelArray, searchUrlArray)
            alertDialog.show()
        }
    }

    private fun createAlertDialog(labelArray: Array<String>, urlArray: Array<String>): AlertDialog {
        val onClickListener = DialogInterface.OnClickListener { _, i ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlArray[i]))
            startActivity(intent)
        }

        return AlertDialog.Builder(requireActivity()).apply {
            setTitle(ActionEnum.OPEN_SEARCH_DIALOG.stringResource)
            setNegativeButton(R.string.close_dialog_label) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            setItems(labelArray, onClickListener)
        }.create()
    }
}