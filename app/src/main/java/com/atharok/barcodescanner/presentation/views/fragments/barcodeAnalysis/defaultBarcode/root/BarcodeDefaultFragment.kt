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
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentBarcodeDefaultBinding
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.atharok.barcodescanner.domain.entity.product.NoneProduct
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_MESSAGE_ERROR_KEY
import com.atharok.barcodescanner.common.extentions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.AboutBarcodeFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeContentsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeErrorApiFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class BarcodeDefaultFragment: ProductBarcodeFragment<BarcodeProduct>() {

    private var _binding: FragmentBarcodeDefaultBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeDefaultBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeProduct){

        viewBinding.fragmentBarcodeDefaultOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureAboutBarcodeEntitledLayout()
        configureBarcodeContentsFragment()
        configureAboutBarcodeFragment()

        val errorMessage: String? = arguments?.getString(BARCODE_MESSAGE_ERROR_KEY)
        when {
            errorMessage != null -> {
                configureProductSearchApiEntitledLayout()
                configureBarcodeErrorApiFragment()
            }
            product.barcode.is1DProductBarcodeFormat && get<SettingsManager>().useSearchOnApiKey -> {
                configureProductSearchApiEntitledLayout()
                configureBarcodeNotFoundApi(product)
            }
            else -> {
                viewBinding.fragmentBarcodeDefaultProductSearchApiEntitledLayout.visibility = View.GONE
                viewBinding.fragmentBarcodeDefaultProductSearchApiFrameLayout.visibility = View.GONE
            }
        }
    }

    private fun configureAboutBarcodeEntitledLayout(){
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.fragmentBarcodeDefaultAboutBarcodeEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureProductSearchApiEntitledLayout(){
        val entitled: String = getString(R.string.product_search_label)
        viewBinding.fragmentBarcodeDefaultProductSearchApiEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureBarcodeContentsFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeDefaultBarcodeContentsFrameLayout.id,
        fragmentClass = BarcodeContentsFragment::class,
        args = arguments
    )

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeDefaultAboutBarcodeFrameLayout.id,
        fragmentClass = AboutBarcodeFragment::class,
        args = arguments
    )

    /**
     * Si une erreur s'est produit pendant la recherche sur les APIs
     */
    private fun configureBarcodeErrorApiFragment() = applyFragment(
        containerViewId = viewBinding.fragmentBarcodeDefaultProductSearchApiFrameLayout.id,
        fragmentClass = BarcodeErrorApiFragment::class,
        args = arguments
    )

    /**
     * Si pas d'erreur mais pas trouvé dans les APIs distantes
     */
    private fun configureBarcodeNotFoundApi(barcodeProduct: BarcodeProduct) {
        val api = if(barcodeProduct.barcode.isBookBarcode())
            getString(R.string.open_library_label) else getString(R.string.open_food_facts_label)

        configureBarcodeNotFoundApiFragment(getString(R.string.barcode_not_found_on_api_label, api))
    }

    private fun configureBarcodeNotFoundApiFragment(contents: String) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentBarcodeDefaultProductSearchApiFrameLayout,
        title = getString(R.string.information_label),
        contents = contents,
        iconDrawableResource = R.drawable.outline_info_24
    )

    companion object {
        fun newInstance(noneProduct: NoneProduct, messageError: String? = null) = BarcodeDefaultFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, noneProduct)
                if(messageError != null)
                    putString(BARCODE_MESSAGE_ERROR_KEY, messageError)
            }
        }
    }
}