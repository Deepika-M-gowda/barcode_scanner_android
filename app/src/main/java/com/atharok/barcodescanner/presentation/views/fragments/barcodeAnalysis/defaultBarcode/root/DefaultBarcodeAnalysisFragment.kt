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
import android.view.*
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentDefaultBarcodeAnalysisBinding
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class DefaultBarcodeAnalysisFragment : BarcodeAnalysisFragment<DefaultBarcodeAnalysis>() {
    private var _binding: FragmentDefaultBarcodeAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_activity_barcode_analysis, menu)

        // On retire les menus inutiles
        menu.removeItem(R.id.menu_activity_barcode_analysis_download_from_apis)
        menu.removeItem(R.id.menu_activity_barcode_analysis_product_source_api_info_item)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.menu_activity_barcode_analysis_about_barcode_item -> {
                startBarcodeDetailsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDefaultBarcodeAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: DefaultBarcodeAnalysis) {
        viewBinding.fragmentDefaultBarcodeAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()
        configureAboutBarcodeFragment()
    }

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentDefaultBarcodeAnalysisAboutBarcodeFrameLayout.id,
        fragmentClass = BarcodeAnalysisInformationFragment::class,
        args = arguments
    )

    companion object {
        fun newInstance(barcodeAnalysis: DefaultBarcodeAnalysis) = DefaultBarcodeAnalysisFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, barcodeAnalysis)
            }
        }
    }
}