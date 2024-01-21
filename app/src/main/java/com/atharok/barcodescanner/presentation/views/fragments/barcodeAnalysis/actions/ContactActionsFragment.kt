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
import com.atharok.barcodescanner.presentation.intent.createAddContactIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.AddressBookParsedResult
import com.google.zxing.client.result.ParsedResult

class ContactActionsFragment: AbstractParsedResultActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(parsedResult){
            is AddressBookParsedResult -> configureContactActions(barcode, parsedResult)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configureContactActions(barcode: Barcode, parsedResult: AddressBookParsedResult) = arrayOf(
        ActionItem(R.string.action_add_to_contacts, R.drawable.baseline_contacts_24, addToContact(parsedResult))
    ) + configureDefaultActions(barcode)

    // Actions

    private fun addToContact(parsedResult: AddressBookParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createAddContactIntent(parsedResult)
            mStartActivity(intent)
        }
    }
}