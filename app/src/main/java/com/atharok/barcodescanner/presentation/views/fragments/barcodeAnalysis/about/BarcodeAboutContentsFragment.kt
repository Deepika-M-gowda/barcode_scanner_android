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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeAboutContentsBinding
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixAgendaFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixContactFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixEmailFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixLocalisationFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixPhoneFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixSmsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixUriFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeMatrixWifiFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.contents.BarcodeTextFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

class BarcodeAboutContentsFragment: BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private var _binding: FragmentBarcodeAboutContentsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAboutContentsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(analysis: BarcodeAnalysis) {
        val barcode = analysis.barcode

        val parsedResult: ParsedResult = get {
            parametersOf(barcode.contents, barcode.getBarcodeFormat())
        }

        val displayResult = parsedResult.displayResult

        configureHeaderEntitled(parsedResult, displayResult)
        configureHeaderIcon(barcode.getBarcodeFormat())
        configureBarcodeContentsFragment(parsedResult, displayResult)
    }

    private fun configureHeaderEntitled(parsedResult: ParsedResult, displayResult: String?) {
        val entitledStringResource: Int = if(displayResult.isNullOrBlank()){
            R.string.bar_code_content_label
        } else {
            when (parsedResult.type) {
                ParsedResultType.ADDRESSBOOK, ParsedResultType.EMAIL_ADDRESS, ParsedResultType.URI,
                ParsedResultType.GEO, ParsedResultType.TEL, ParsedResultType.SMS,
                ParsedResultType.CALENDAR, ParsedResultType.WIFI -> R.string.bar_code_analysis_label
                else -> R.string.bar_code_content_label
            }
        }

        viewBinding.fragmentBarcodeAboutContentsTitleTextView.text = getString(entitledStringResource)
    }

    private fun configureHeaderIcon(barcodeFormat: BarcodeFormat) {
        val barCodeIconDrawableResource: Int = when(barcodeFormat) {
            BarcodeFormat.QR_CODE -> R.drawable.baseline_qr_code_24
            BarcodeFormat.AZTEC -> R.drawable.ic_aztec_code_24
            BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_code_24
            BarcodeFormat.PDF_417 -> R.drawable.ic_pdf_417_code_24
            else -> R.drawable.ic_bar_code_24
        }

        viewBinding.fragmentBarcodeAboutContentsIconImageView.setImageResource(barCodeIconDrawableResource)
    }

    private fun configureBarcodeContentsFragment(parsedResult: ParsedResult, displayResult: String?) {

        val fragmentKClass = if(displayResult.isNullOrEmpty()) {
            BarcodeTextFragment::class
        } else {
            when (parsedResult.type) {
                ParsedResultType.TEXT -> BarcodeTextFragment::class
                ParsedResultType.ADDRESSBOOK -> BarcodeMatrixContactFragment::class
                ParsedResultType.EMAIL_ADDRESS -> BarcodeMatrixEmailFragment::class
                ParsedResultType.URI -> BarcodeMatrixUriFragment::class
                ParsedResultType.GEO -> BarcodeMatrixLocalisationFragment::class
                ParsedResultType.TEL -> BarcodeMatrixPhoneFragment::class
                ParsedResultType.SMS -> BarcodeMatrixSmsFragment::class
                ParsedResultType.CALENDAR -> BarcodeMatrixAgendaFragment::class
                ParsedResultType.WIFI -> BarcodeMatrixWifiFragment::class
                else -> BarcodeTextFragment::class
            }
        }

        applyFragment(
            containerViewId = viewBinding.fragmentBarcodeAboutContentsFrameLayout.id,
            fragmentClass = fragmentKClass,
            args = arguments
        )
    }
}