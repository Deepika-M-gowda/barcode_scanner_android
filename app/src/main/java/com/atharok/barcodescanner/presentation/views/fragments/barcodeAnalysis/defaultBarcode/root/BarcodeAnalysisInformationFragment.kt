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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeAnalysisInformationBinding
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisAboutFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisContentsFragment
import org.koin.android.ext.android.get

/**
 * Fragment implémenté dans DefaultBarcodeAnalysisFragment, ProductAnalysisFragment,
 * BookAnalysisFragment et FoodAnalysisRootOverviewFragment.
 */
class BarcodeAnalysisInformationFragment: BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private var _binding: FragmentBarcodeAnalysisInformationBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAnalysisInformationBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeAnalysis){

        //viewBinding.fragmentBarcodeDefaultOuterView.fixAnimateLayoutChangesInNestedScroll()
        viewBinding.root.fixAnimateLayoutChangesInNestedScroll()

        configureAboutBarcodeEntitledLayout()
        configureBarcodeContentsFragment()
        configureAboutBarcodeFragment()
    }

    private fun configureAboutBarcodeEntitledLayout(){
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.fragmentBarcodeAnalysisInformationAboutBarcodeEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureBarcodeContentsFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeAnalysisInformationBarcodeContentsFrameLayout.id,
        fragmentClass = BarcodeAnalysisContentsFragment::class,
        args = arguments
    )

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeAnalysisInformationAboutBarcodeFrameLayout.id,
        fragmentClass = BarcodeAnalysisAboutFragment::class,
        args = arguments
    )

    companion object {
        fun newInstance(barcodeAnalysis: DefaultBarcodeAnalysis) = BarcodeAnalysisInformationFragment()
            .apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, barcodeAnalysis)
            }
        }
    }
}