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
import android.view.*
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.databinding.FragmentProductAnalysisBinding
import com.atharok.barcodescanner.domain.entity.product.ApiError
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisErrorApiFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root.BarcodeAnalysisInformationFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class ProductAnalysisFragment : BarcodeAnalysisFragment<DefaultBarcodeAnalysis>() {

    private var _binding: FragmentProductAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_activity_barcode_analysis, menu)

        // On retire les menus inutile
        menu.removeItem(R.id.menu_activity_barcode_analysis_product_source_api_info_item)

        arguments?.getSerializable(API_ERROR_KEY, ApiError::class.java)?.let { apiError ->
            if(apiError == ApiError.NO_RESULT)
                menu.removeItem(R.id.menu_activity_barcode_analysis_download_from_apis)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.menu_activity_barcode_analysis_download_from_apis -> {
                requireActivity().apply {
                    intent.putExtra(IGNORE_USE_SEARCH_ON_API_SETTING_KEY, true)
                    recreate()
                }

                true
            }

            R.id.menu_activity_barcode_analysis_about_barcode_item -> {
                startBarcodeDetailsActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: DefaultBarcodeAnalysis) {

        viewBinding.fragmentProductAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureAboutBarcodeFragment()

        arguments?.getSerializable(API_ERROR_KEY, ApiError::class.java)?.let { apiError ->
            when(apiError){
                ApiError.NO_INTERNET_PERMISSION, ApiError.ERROR -> {
                    configureProductSearchApiEntitledLayout()
                    configureBarcodeErrorApiFragment()
                }
                ApiError.NO_API_RESEARCH -> {
                    viewBinding.fragmentProductAnalysisSearchApiEntitledLayout.visibility = View.GONE
                    viewBinding.fragmentProductAnalysisSearchApiFrameLayout.visibility = View.GONE
                }
                ApiError.NO_RESULT -> {
                    configureProductSearchApiEntitledLayout()
                    configureBarcodeNotFoundApi(product)
                }
            }
        }
    }

    private fun configureProductSearchApiEntitledLayout(){
        val entitled: String = getString(R.string.product_search_label)
        viewBinding.fragmentProductAnalysisProductSearchApiEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisAboutBarcodeFrameLayout.id,
        fragmentClass = BarcodeAnalysisInformationFragment::class,
        args = arguments
    )

    /**
     * Si une erreur s'est produit pendant la recherche sur les APIs
     */
    private fun configureBarcodeErrorApiFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisSearchApiFrameLayout.id,
        fragmentClass = BarcodeAnalysisErrorApiFragment::class,
        args = arguments
    )

    /**
     * Si pas d'erreur mais pas trouvé dans les APIs distantes
     */
    private fun configureBarcodeNotFoundApi(barcodeAnalysis: BarcodeAnalysis) {
        val api = if(barcodeAnalysis.barcode.isBookBarcode())
            getString(R.string.open_library_label) else getString(R.string.open_food_facts_label)

        configureBarcodeNotFoundApiFragment(getString(R.string.barcode_not_found_on_api_label, api))
    }

    private fun configureBarcodeNotFoundApiFragment(contents: String) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentProductAnalysisSearchApiFrameLayout,
        title = getString(R.string.information_label),
        contents = contents,
        iconDrawableResource = R.drawable.outline_info_24
    )

    companion object {
        fun newInstance(defaultBarcodeAnalysis: DefaultBarcodeAnalysis, apiError: ApiError, errorMessage: String?) = ProductAnalysisFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, defaultBarcodeAnalysis)
                putSerializable(API_ERROR_KEY, apiError)
                errorMessage?.let {
                    putString(BARCODE_MESSAGE_ERROR_KEY, it)
                }
            }
        }
    }
}