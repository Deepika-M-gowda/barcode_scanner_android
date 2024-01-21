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
import com.atharok.barcodescanner.presentation.intent.createAddSmsNumberIntent
import com.atharok.barcodescanner.presentation.intent.createCallSmsNumberIntent
import com.atharok.barcodescanner.presentation.intent.createSendSmsToSmsNumberIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.SMSParsedResult

class SmsActionsFragment: AbstractParsedResultActionsFragment() {
    override fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem> {
        return when(parsedResult){
            is SMSParsedResult -> configureSmsActions(barcode, parsedResult)
            else -> configureDefaultActions(barcode)
        }
    }

    private fun configureSmsActions(barcode: Barcode, parsedResult: SMSParsedResult) = arrayOf(
        ActionItem(R.string.action_send_sms_label, R.drawable.baseline_textsms_24, sendSms(parsedResult)),
        ActionItem(R.string.action_call_phone_label, R.drawable.baseline_call_24, callSmsPhone(parsedResult)),
        ActionItem(R.string.action_add_to_contacts, R.drawable.baseline_contacts_24, addSmsPhoneNumberToContact(parsedResult))
    ) + configureDefaultActions(barcode)

    // Actions

    private fun callSmsPhone(parsedResult: SMSParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createCallSmsNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun sendSms(parsedResult: SMSParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createSendSmsToSmsNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun addSmsPhoneNumberToContact(parsedResult: SMSParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createAddSmsNumberIntent(parsedResult)
            mStartActivity(intent)
        }
    }
}