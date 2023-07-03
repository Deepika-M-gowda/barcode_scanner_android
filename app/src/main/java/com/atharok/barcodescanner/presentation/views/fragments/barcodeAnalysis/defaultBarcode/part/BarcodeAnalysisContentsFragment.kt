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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION_ID
import com.atharok.barcodescanner.databinding.FragmentExpandableViewBinding
import com.atharok.barcodescanner.databinding.TemplateEntitledViewBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixAgendaFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixContactFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixEmailFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixLocalisationFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixPhoneFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixSmsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixUriFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix.BarcodeMatrixWifiFragment
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 */
class BarcodeAnalysisContentsFragment: BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    ) // close in BarcodeAnalysisActivity

    private var _binding: FragmentExpandableViewBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerEntitledTemplateBinding: TemplateEntitledViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpandableViewBinding.inflate(inflater, container, false)
        configureBarcodeContentsTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureBarcodeContentsTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentExpandableViewTemplate

        expandableViewTemplate.root.open() // L'ExpandableView est ouvert par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout

        headerEntitledTemplateBinding = TemplateEntitledViewBinding.inflate(inflater, parentHeader, true)
    }

    override fun start(product: BarcodeAnalysis) {

        val barcode = product.barcode

        val parsedResult = barcodeAnalysisScope.get<ParsedResult> {
            parametersOf(barcode.contents, barcode.getBarcodeFormat())
        }

        val displayResult = parsedResult.displayResult

        configureHeaderEntitled(parsedResult, displayResult)
        configureHeaderIcon(barcode.getBarcodeFormat())
        configureBarcodeContentsFragment(parsedResult, displayResult)
    }

    private fun configureHeaderEntitled(parsedResult: ParsedResult, displayResult: String?){
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

        headerEntitledTemplateBinding.templateEntitledViewTextView.root.text = getString(entitledStringResource)
    }

    private fun configureHeaderIcon(barcodeFormat: BarcodeFormat){
        val barCodeIconDrawableResource: Int = when(barcodeFormat){
            BarcodeFormat.QR_CODE -> R.drawable.baseline_qr_code_24
            BarcodeFormat.AZTEC -> R.drawable.ic_aztec_code_24
            BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_code_24
            BarcodeFormat.PDF_417 -> R.drawable.ic_pdf_417_code_24
            else -> R.drawable.ic_bar_code_24
        }

        headerEntitledTemplateBinding.templateEntitledViewIconImageView.setImageResource(barCodeIconDrawableResource)
    }

    private fun configureBarcodeContentsFragment(parsedResult: ParsedResult, displayResult: String?){

        val fragmentKClass = if(displayResult.isNullOrEmpty()){
            BarcodeAnalysisTextFragment::class
        }else {
            when (parsedResult.type) {
                ParsedResultType.TEXT -> BarcodeAnalysisTextFragment::class
                ParsedResultType.ADDRESSBOOK -> BarcodeMatrixContactFragment::class
                ParsedResultType.EMAIL_ADDRESS -> BarcodeMatrixEmailFragment::class
                ParsedResultType.URI -> BarcodeMatrixUriFragment::class
                ParsedResultType.GEO -> BarcodeMatrixLocalisationFragment::class
                ParsedResultType.TEL -> BarcodeMatrixPhoneFragment::class
                ParsedResultType.SMS -> BarcodeMatrixSmsFragment::class
                ParsedResultType.CALENDAR -> BarcodeMatrixAgendaFragment::class
                ParsedResultType.WIFI -> BarcodeMatrixWifiFragment::class
                else -> BarcodeAnalysisTextFragment::class
            }
        }

        val frameLayout = viewBinding.fragmentExpandableViewTemplate.templateExpandableViewBodyFrameLayout
        applyFragment(frameLayout.id, fragmentKClass, arguments)
    }
}