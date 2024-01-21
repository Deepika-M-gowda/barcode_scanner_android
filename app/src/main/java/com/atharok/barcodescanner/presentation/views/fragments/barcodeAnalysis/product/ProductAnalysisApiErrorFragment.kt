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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentProductAnalysisApiErrorBinding
import com.atharok.barcodescanner.databinding.TemplateEntitledViewBinding
import com.atharok.barcodescanner.databinding.TemplateWarningViewBinding
import com.atharok.barcodescanner.domain.entity.analysis.UnknownProductBarcodeAnalysis
import com.atharok.barcodescanner.domain.library.InternetChecker
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class ProductAnalysisApiErrorFragment: BarcodeAnalysisFragment<UnknownProductBarcodeAnalysis>() {

    private var _binding: FragmentProductAnalysisApiErrorBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerEntitledTemplateBinding: TemplateEntitledViewBinding
    private lateinit var bodyWarningTemplateBinding: TemplateWarningViewBinding

    private val internetChecker: InternetChecker by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductAnalysisApiErrorBinding.inflate(inflater, container, false)
        configureWarningTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureWarningTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentBarcodeAnalysisErrorApiWarningExpandableViewTemplate

        expandableViewTemplate.root.open() // L'ExpandableView est ouvert par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody = expandableViewTemplate.templateExpandableViewBodyFrameLayout

        headerEntitledTemplateBinding = TemplateEntitledViewBinding.inflate(inflater, parentHeader, true)
        bodyWarningTemplateBinding = TemplateWarningViewBinding.inflate(inflater, parentBody, true)
    }

    override fun start(product: UnknownProductBarcodeAnalysis) {
        configureHeaderEntitledAndIcon()
        configureInformationTextViewError()
        configureMessageTextViewError(product.message)
    }

    private fun configureHeaderEntitledAndIcon(){
        headerEntitledTemplateBinding.templateEntitledViewTextView.root.text = getString(R.string.warning_label)
        headerEntitledTemplateBinding.templateEntitledViewIconImageView.setImageResource(R.drawable.baseline_warning_24)
    }

    private fun configureInformationTextViewError(){
        val isConnected = internetChecker.isInternetAvailable()

        val msg = if(!isConnected)
            getString(R.string.scan_error_internet_information_label)
        else getString(R.string.scan_error_information_label)

        bodyWarningTemplateBinding.templateWarningViewInformationTextView.text = msg
    }

    private fun configureMessageTextViewError(message: String?) = displayText(
        textView = bodyWarningTemplateBinding.templateWarningViewErrorMessageTextView,
        layout = bodyWarningTemplateBinding.templateWarningViewErrorMessageLayout,
        text =  message
    )
}