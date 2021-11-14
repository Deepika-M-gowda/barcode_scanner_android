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
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixSmsBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.SMSParsedResult

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixSmsFragment : AbstractBarcodeMatrixFragment() {
    private var _binding: FragmentBarcodeMatrixSmsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixSmsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeProduct, parsedResult: ParsedResult) {

        if(parsedResult is SMSParsedResult && parsedResult.type == ParsedResultType.SMS) {
            configurePhoneNumber(parsedResult.numbers)
            configureSmsSubject(parsedResult.subject)
            configureSmsBody(parsedResult.body)
        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configurePhoneNumber(phoneNumbers: Array<String?>?) = displayArray(
        textView = viewBinding.fragmentBarcodeMatrixSmsNumberTextView,
        layout = viewBinding.fragmentBarcodeMatrixSmsNumberLayout,
        array = phoneNumbers
    )

    private fun configureSmsSubject(subject: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixSmsSubjectTextView,
        layout = viewBinding.fragmentBarcodeMatrixSmsSubjectLayout,
        text = subject
    )

    private fun configureSmsBody(message: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixSmsBodyTextView,
        layout = viewBinding.fragmentBarcodeMatrixSmsBodyLayout,
        text = message
    )
}