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

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_DEFAULT_SIZE
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeAboutOverviewBinding
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.AbstractActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeImageEditor.BarcodeImageFragment
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import kotlin.reflect.KClass

/**
 * Contains:
 * - Barcode content (BarcodeAboutContentsFragment)
 * - Additional information (BarcodeAboutMoreInfoFragment)
 * - Actions (subclass of AbstractActionsFragment)
 */
class BarcodeAboutOverviewFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private var _binding: FragmentBarcodeAboutOverviewBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAboutOverviewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(analysis: BarcodeAnalysis) {
        viewBinding.root.fixAnimateLayoutChangesInNestedScroll()
        configureBarcodeAboutImageFragment(analysis.barcode)
        configureBarcodeAboutContentsFragment()
        configureBarcodeAboutMoreInfoFragment()
        configureBarcodeActionsFragment(analysis.barcode.getBarcodeType())
    }

    private fun configureBarcodeAboutImageFragment(barcode: Barcode) {
        if(settingsManager.shouldDisplayBarcodeInResultsView) {
            val properties = BarcodeImageGeneratorProperties(
                contents = barcode.contents,
                format = barcode.getBarcodeFormat(),
                qrCodeErrorCorrectionLevel = barcode.getQrCodeErrorCorrectionLevel(),
                size = BARCODE_IMAGE_DEFAULT_SIZE,
                frontColor = Color.BLACK,
                backgroundColor = Color.WHITE
            )

            applyFragment(
                containerViewId = viewBinding.fragmentBarcodeAboutOverviewBarcodeImageFrameLayout.id,
                fragmentClass = BarcodeImageFragment::class,
                args = Bundle().apply {
                    putSerializable(BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY, properties)
                }
            )
        } else {
            viewBinding.fragmentBarcodeAboutOverviewBarcodeImageFrameLayout.visibility = View.GONE
        }
    }

    private fun configureBarcodeAboutContentsFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeAboutOverviewBarcodeContentsFrameLayout.id,
        fragmentClass = BarcodeAboutContentsFragment::class,
        args = arguments
    )

    private fun configureBarcodeAboutMoreInfoFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeAboutOverviewMoreInfoFrameLayout.id,
        fragmentClass = BarcodeAboutMoreInfoFragment::class,
        args = arguments
    )

    private fun configureBarcodeActionsFragment(barcodeType: BarcodeType) = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeAboutOverviewBarcodeActionsFrameLayout.id,
        fragmentClass = get<KClass<out AbstractActionsFragment>> { parametersOf(barcodeType) },
        args = arguments
    )
}