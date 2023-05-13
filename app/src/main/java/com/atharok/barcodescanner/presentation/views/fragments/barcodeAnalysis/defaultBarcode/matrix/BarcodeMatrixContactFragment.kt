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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixContactBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.google.zxing.client.result.AddressBookParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixContactFragment : AbstractBarcodeMatrixFragment() {

    private var _binding: FragmentBarcodeMatrixContactBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixContactBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeAnalysis, parsedResult: ParsedResult) {
        if(parsedResult is AddressBookParsedResult && parsedResult.type == ParsedResultType.ADDRESSBOOK) {
            configureName(parsedResult.names ?: parsedResult.nicknames)
            configureOrganization(parsedResult.org)
            configureTitle(parsedResult.title)
            configurePhone(parsedResult.phoneNumbers, parsedResult.phoneTypes)
            configureMail(parsedResult.emails, parsedResult.emailTypes)
            configureAddress(parsedResult.addresses)
            configureNotes(parsedResult.note)
        } else {
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureName(names: Array<String?>?) = configureTextArray(
        textView = viewBinding.fragmentBarcodeMatrixContactNameTextView,
        layout = viewBinding.fragmentBarcodeMatrixContactNameLayout,
        array = names
    )

    private fun configureOrganization(org: String?) = configureText(
        textView = viewBinding.fragmentBarcodeMatrixContactOrganizationTextView,
        layout = viewBinding.fragmentBarcodeMatrixContactOrganizationLayout,
        text = org
    )

    private fun configureTitle(title: String?) = configureText(
        textView = viewBinding.fragmentBarcodeMatrixContactTitleTextView,
        layout = viewBinding.fragmentBarcodeMatrixContactTitleLayout,
        text = title
    )

    private fun configurePhone(phoneNumbers: Array<String?>?, phoneTypes: Array<String?>?){

        if(phoneNumbers.isNullOrEmpty()){
            viewBinding.fragmentBarcodeMatrixContactPhoneLayout.visibility = View.GONE
        } else {

            // Phone 1
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactPhone1TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactPhoneType1TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactPhone1Layout,
                contact = if(phoneNumbers.isNotEmpty()) phoneNumbers[0] else null,
                type = if(!phoneTypes.isNullOrEmpty()) phoneTypes[0] else null
            )

            // Phone 2
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactPhone2TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactPhoneType2TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactPhone2Layout,
                contact = if(phoneNumbers.size>1) phoneNumbers[1] else null,
                type = if(phoneTypes != null && phoneTypes.size>1) phoneTypes[1] else null
            )

            // Phone 3
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactPhone3TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactPhoneType3TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactPhone3Layout,
                contact = if(phoneNumbers.size>2) phoneNumbers[2] else null,
                type = if(phoneTypes != null && phoneTypes.size>2) phoneTypes[2] else null
            )
        }
    }

    private fun configureMail(mails: Array<String?>?, mailTypes: Array<String?>?){

        if(mails.isNullOrEmpty()){
            viewBinding.fragmentBarcodeMatrixContactEmailLayout.visibility = View.GONE
        } else {

            // Mail 1
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactEmail1TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactEmailType1TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactEmail1Layout,
                contact = if(mails.isNotEmpty()) mails[0] else null,
                type = if(!mailTypes.isNullOrEmpty()) mailTypes[0] else null
            )

            // Mail 2
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactEmail2TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactEmailType2TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactEmail2Layout,
                contact = if(mails.size>1) mails[1] else null,
                type = if(mailTypes != null && mailTypes.size>1) mailTypes[1] else null
            )

            // Mail 3
            configureContact(
                nameTextView = viewBinding.fragmentBarcodeMatrixContactEmail3TextView,
                typeTextView = viewBinding.fragmentBarcodeMatrixContactEmailType3TextView,
                layout = viewBinding.fragmentBarcodeMatrixContactEmail3Layout,
                contact = if(mails.size>2) mails[2] else null,
                type = if(mailTypes != null && mailTypes.size>2) mailTypes[2] else null
            )
        }
    }

    private fun configureAddress(addresses: Array<String?>?) = configureTextArray(
        textView = viewBinding.fragmentBarcodeMatrixContactAddressTextView,
        layout = viewBinding.fragmentBarcodeMatrixContactAddressLayout,
        array = addresses
    )

    private fun configureNotes(notes: String?) = configureText(
        textView = viewBinding.fragmentBarcodeMatrixContactNotesTextView,
        layout = viewBinding.fragmentBarcodeMatrixContactNotesLayout,
        text = notes
    )

    private fun configureContact(
        nameTextView: TextView,
        typeTextView: TextView,
        layout: View,
        contact: String?,
        type: String?){

        configureText(nameTextView, layout, contact)

        if(!type.isNullOrBlank()){
            val typeConcat = "($type)"
            typeTextView.text = typeConcat
        } else typeTextView.visibility = View.GONE
    }
}