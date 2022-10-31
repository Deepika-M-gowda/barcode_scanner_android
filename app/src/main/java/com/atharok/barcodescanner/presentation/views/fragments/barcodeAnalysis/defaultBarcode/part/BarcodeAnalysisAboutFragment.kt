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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeAnalysisAboutBinding
import com.atharok.barcodescanner.databinding.TemplateAboutBarcodeBinding
import com.atharok.barcodescanner.databinding.TemplateEntitledViewBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.google.zxing.BarcodeFormat

/**
 * A simple [Fragment] subclass.
 */
class BarcodeAnalysisAboutFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private var _binding: FragmentBarcodeAnalysisAboutBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerEntitledTemplateBinding: TemplateEntitledViewBinding
    private lateinit var bodyAboutBarcodeTemplateBinding: TemplateAboutBarcodeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAnalysisAboutBinding.inflate(inflater, container, false)
        configureAboutBarcodeTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureAboutBarcodeTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentBarcodeAnalysisAboutExpandableViewTemplate

        expandableViewTemplate.root.open() // L'ExpandableView est ouvert par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody = expandableViewTemplate.templateExpandableViewBodyFrameLayout

        headerEntitledTemplateBinding = TemplateEntitledViewBinding.inflate(inflater, parentHeader, true)
        bodyAboutBarcodeTemplateBinding = TemplateAboutBarcodeBinding.inflate(inflater, parentBody, true)
    }

    override fun start(product: BarcodeAnalysis) {
        configureHeaderEntitledAndIcon()
        configureFormat(product)
        configureOrigin(product)
        configureDescription(product)
    }

    private fun configureHeaderEntitledAndIcon(){
        headerEntitledTemplateBinding.templateEntitledViewTextView.root.text = getString(R.string.about_barcode_information_label)
        headerEntitledTemplateBinding.templateEntitledViewIconImageView.setImageResource(R.drawable.outline_info_24)
    }

    private fun configureFormat(barcodeAnalysis: BarcodeAnalysis){
        val formatName = barcodeAnalysis.barcode.formatName.replace("_", " ")
        val format = getString(R.string.about_barcode_format_label, formatName)
        bodyAboutBarcodeTemplateBinding.templateAboutBarcodeFormatTextView.text = format
    }

    private fun configureOrigin(barcodeAnalysis: BarcodeAnalysis){
        val origin = barcodeAnalysis.barcode.country

        if(origin != null) {
            bodyAboutBarcodeTemplateBinding.templateAboutBarcodeOriginFlagImageView.setImageResource(origin.drawableResource)
            displayText(
                textView = bodyAboutBarcodeTemplateBinding.templateAboutBarcodeOriginCountryTextView,
                layout = bodyAboutBarcodeTemplateBinding.templateAboutBarcodeOriginLayout,
                text = getString(origin.stringResource)
            )
        }else{
            bodyAboutBarcodeTemplateBinding.templateAboutBarcodeOriginLayout.visibility = View.GONE
        }
    }

    private fun configureDescription(barcodeAnalysis: BarcodeAnalysis){
        val text = when(barcodeAnalysis.barcode.getBarcodeFormat()){
            BarcodeFormat.UPC_A -> getString(R.string.bar_code_upc_a_description_label)
            BarcodeFormat.UPC_E -> getString(R.string.bar_code_upc_e_description_label)
            BarcodeFormat.EAN_13 -> getString(R.string.bar_code_ean_13_description_label)
            BarcodeFormat.EAN_8 -> getString(R.string.bar_code_ean_8_description_label)
            BarcodeFormat.CODE_39 -> getString(R.string.bar_code_code_39_description_label)
            BarcodeFormat.CODE_93 -> getString(R.string.bar_code_code_93_description_label)
            BarcodeFormat.CODE_128 -> getString(R.string.bar_code_code_128_description_label)
            BarcodeFormat.CODABAR -> getString(R.string.bar_code_codabar_description_label)
            BarcodeFormat.ITF -> getString(R.string.bar_code_itf_description_label)
            else -> null
        }

        displayText(
            textView = bodyAboutBarcodeTemplateBinding.fragmentAboutBarcodeDescriptionTextView,
            layout = bodyAboutBarcodeTemplateBinding.templateAboutBarcodeDescriptionLayout,
            text = text
        )
    }
}