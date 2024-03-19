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
import com.atharok.barcodescanner.presentation.intent.createAddPhoneNumberIntent
import com.atharok.barcodescanner.presentation.intent.createCallPhoneNumberIntent
import com.atharok.barcodescanner.presentation.intent.createSendSmsToPhoneNumberIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.TelParsedResult

class PhoneActionsFragment: AbstractParsedResultActionsFragment() {
    override fun configureActionItems(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult is TelParsedResult) {
            addActionItem(configureCallPhoneActionItem(parsedResult))
            addActionItem(configureSendSmsActionItem(parsedResult))
            addActionItem(configureAddToContactsActionItem(parsedResult))
        }
        addActionItem(configureSearchOnWebActionItem(barcode))
        addActionItem(configureShareTextActionItem(barcode))
        addActionItem(configureCopyTextActionItem(barcode))
        addActionItem(configureModifyBarcodeActionItem(barcode))
    }

    private fun configureCallPhoneActionItem(parsedResult: TelParsedResult): ActionItem {
        return ActionItem(
            textRes = R.string.action_call_phone_label,
            imageRes = R.drawable.baseline_call_24,
            listener = callPhone(parsedResult)
        )
    }

    private fun configureSendSmsActionItem(parsedResult: TelParsedResult): ActionItem {
        return ActionItem(
            textRes = R.string.action_send_sms_label,
            imageRes = R.drawable.baseline_textsms_24,
            listener = sendSmsToPhoneNumber(parsedResult)
        )
    }

    private fun configureAddToContactsActionItem(parsedResult: TelParsedResult): ActionItem {
        return ActionItem(
            textRes = R.string.action_add_to_contacts,
            imageRes = R.drawable.baseline_contacts_24,
            listener = addPhoneNumberToContacts(parsedResult)
        )
    }

    private fun callPhone(parsedResult: TelParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createCallPhoneNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun sendSmsToPhoneNumber(parsedResult: TelParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createSendSmsToPhoneNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun addPhoneNumberToContacts(parsedResult: TelParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createAddPhoneNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }
}