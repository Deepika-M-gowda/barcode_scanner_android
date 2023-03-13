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
import com.atharok.barcodescanner.presentation.intent.createAddEmailIntent
import com.atharok.barcodescanner.presentation.intent.createSendEmailIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.ParsedResult

class EmailActionsFragment: AbstractActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(parsedResult){
            is EmailAddressParsedResult -> configureEmailActions(barcode, parsedResult)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configureEmailActions(barcode: Barcode, parsedResult: EmailAddressParsedResult) = arrayOf(
        ActionItem(R.string.action_send_mail_label, R.drawable.baseline_mail_24, sendEmail(parsedResult)),
        ActionItem(R.string.action_add_to_contacts, R.drawable.baseline_contacts_24, addEmailAddressToContact(parsedResult))
    ) + configureDefaultActions(barcode)

    // Actions

    private fun addEmailAddressToContact(parsedResult: EmailAddressParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createAddEmailIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun sendEmail(parsedResult: EmailAddressParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createSendEmailIntent(requireContext(), parsedResult)
            mStartActivity(intent)
        }
    }
}