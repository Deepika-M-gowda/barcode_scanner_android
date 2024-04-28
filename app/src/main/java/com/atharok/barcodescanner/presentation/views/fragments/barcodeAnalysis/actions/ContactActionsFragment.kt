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

import android.content.Intent
import android.view.View
import androidx.core.content.FileProvider
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.presentation.intent.createAddContactIntent
import com.atharok.barcodescanner.presentation.intent.createShareVcfFileIntent
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.AddressBookParsedResult
import com.google.zxing.client.result.ParsedResult
import java.io.File
import java.io.FileOutputStream

class ContactActionsFragment: AbstractParsedResultActionsFragment() {
    override fun configureActionItems(barcode: Barcode, parsedResult: ParsedResult) {
        if(parsedResult is AddressBookParsedResult) {
            addActionItem(configureAddContactActionItem(parsedResult))
            addActionItem(configureShareVcfFileContactActionItem(barcode.contents))
        }
        addActionItem(configureSearchOnWebActionItem(barcode))
        addActionItem(configureShareTextActionItem(barcode))
        addActionItem(configureCopyTextActionItem(barcode))
        addActionItem(configureModifyBarcodeActionItem(barcode))
    }

    private fun configureAddContactActionItem(parsedResult: AddressBookParsedResult): ActionItem {
        return ActionItem(
            textRes = R.string.action_add_to_contacts,
            imageRes = R.drawable.baseline_contacts_24,
            listener = addToContact(parsedResult)
        )
    }

    private fun configureShareVcfFileContactActionItem(vCardStr: String): ActionItem {
        return ActionItem(
            textRes = R.string.action_share_vcf_file,
            imageRes = R.drawable.baseline_share_24,
            listener = shareVcfFileContact(vCardStr)
        )
    }

    private fun addToContact(parsedResult: AddressBookParsedResult): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createAddContactIntent(parsedResult)
            mStartActivity(intent)
        }
    }

    private fun shareVcfFileContact(vCardStr: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            try {
                val vcfFolder = File(requireContext().cacheDir, "vcf")
                vcfFolder.mkdirs()

                val vcfFile = File(vcfFolder, "contact.vcf")
                val fileOutputStream = FileOutputStream(vcfFile)
                fileOutputStream.write(vCardStr.toByteArray())
                fileOutputStream.flush()
                fileOutputStream.close()

                val uri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", vcfFile)

                val intent: Intent = createShareVcfFileIntent(requireContext(), uri)
                requireActivity().startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}