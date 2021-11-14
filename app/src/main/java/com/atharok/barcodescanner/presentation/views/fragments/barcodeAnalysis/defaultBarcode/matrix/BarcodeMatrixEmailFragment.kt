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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixEmailBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.google.zxing.client.result.EmailAddressParsedResult
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixEmailFragment : AbstractBarcodeMatrixFragment() {
    private var _binding: FragmentBarcodeMatrixEmailBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixEmailBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeProduct, parsedResult: ParsedResult) {

        if(parsedResult is EmailAddressParsedResult && parsedResult.type == ParsedResultType.EMAIL_ADDRESS) {
            configureAddress(parsedResult.tos)
            configureCC(parsedResult.cCs)
            configureBCC(parsedResult.bcCs)
            configureSubject(parsedResult.subject)
            configureBody(parsedResult.body)
        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureAddress(addresses: Array<String?>?) = displayArray(
        textView = viewBinding.fragmentBarcodeMatrixEmailAddressTextView,
        layout = viewBinding.fragmentBarcodeMatrixEmailAddressLayout,
        array = addresses
    )

    private fun configureCC(ccs: Array<String?>?) = displayArray(
        textView = viewBinding.fragmentBarcodeMatrixEmailCcTextView,
        layout = viewBinding.fragmentBarcodeMatrixEmailCcLayout,
        array = ccs
    )

    private fun configureBCC(bccs: Array<String?>?) = displayArray(
        textView = viewBinding.fragmentBarcodeMatrixEmailBccTextView,
        layout = viewBinding.fragmentBarcodeMatrixEmailBccLayout,
        array = bccs
    )

    private fun configureSubject(subject: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixEmailSubjectTextView,
        layout = viewBinding.fragmentBarcodeMatrixEmailSubjectLayout,
        text = subject
    )

    private fun configureBody(message: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixEmailBodyTextView,
        layout = viewBinding.fragmentBarcodeMatrixEmailBodyLayout,
        text = message
    )
}