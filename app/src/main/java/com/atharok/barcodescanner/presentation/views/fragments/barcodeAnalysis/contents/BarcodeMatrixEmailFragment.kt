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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extensions.convertToString
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixEmailBinding
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
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

    override fun start(product: BarcodeAnalysis, parsedResult: ParsedResult) {
        if(parsedResult is EmailAddressParsedResult && parsedResult.type == ParsedResultType.EMAIL_ADDRESS) {
            val emailAddressView = viewBinding.fragmentBarcodeMatrixEmailAddressLayout
            val ccView = viewBinding.fragmentBarcodeMatrixEmailCcLayout
            val bccView = viewBinding.fragmentBarcodeMatrixEmailBccLayout
            val subjectView = viewBinding.fragmentBarcodeMatrixEmailSubjectLayout
            val bodyView = viewBinding.fragmentBarcodeMatrixEmailBodyLayout

            emailAddressView.setContentsText(parsedResult.tos?.convertToString())
            ccView.setContentsText(parsedResult.cCs?.convertToString())
            bccView.setContentsText(parsedResult.bcCs?.convertToString())
            subjectView.setContentsText(parsedResult.subject)
            bodyView.setContentsText(parsedResult.body)
        } else {
            viewBinding.root.visibility = View.GONE
        }
    }
}