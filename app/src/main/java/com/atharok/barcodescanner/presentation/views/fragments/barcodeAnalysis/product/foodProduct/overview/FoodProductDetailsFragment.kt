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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.overview

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.atharok.barcodescanner.databinding.FragmentFoodProductDetailsBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.domain.entity.dependencies.Country
import com.atharok.barcodescanner.common.extentions.convertToString
import com.atharok.barcodescanner.common.extentions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.presentation.viewmodel.ExternalFileViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class FoodProductDetailsFragment: ProductBarcodeFragment<FoodProduct>() {

    private val viewModel: ExternalFileViewModel by sharedViewModel()

    private var _binding: FragmentFoodProductDetailsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodProductDetailsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodProduct) {
        viewBinding.root.fixAnimateLayoutChangesInNestedScroll()
        configureCategories(product)
        configurePackaging(product)
        configureStores(product)

        observeCountries(product)
    }

    private fun configureCategories(foodProduct: FoodProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentFoodProductCategoriesFrameLayout,
        title = getString(R.string.categories_label),
        contents = foodProduct.categories
    )

    private fun configurePackaging(foodProduct: FoodProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentFoodProductPackagingFrameLayout,
        title = getString(R.string.packaging_label),
        contents = foodProduct.packaging
    )

    private fun configureStores(foodProduct: FoodProduct) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentFoodProductStoresFrameLayout,
        title = getString(R.string.stores_label),
        contents = foodProduct.stores
    )

    private fun configureOriginsCountries(countries: String?) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentFoodProductOriginsCountriesFrameLayout,
        title = getString(R.string.origins_label),
        contents = countries
    )

    private fun configureSalesCountries(countries: String?) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentFoodProductSalesCountriesFrameLayout,
        title = getString(R.string.countries_label),
        contents = countries
    )

    private fun observeCountries(foodProduct: FoodProduct){

        val countriesTags = foodProduct.countriesTagList

        if(countriesTags != null && countriesTags.isNotEmpty()) {

            viewModel.obtainCountriesList(countriesTags).observe(viewLifecycleOwner) {

                val salesCountriesStr: String?
                val originsCountriesStr: String?
                if (it.isNotEmpty()) {
                    salesCountriesStr =
                        obtainCountriesString(foodProduct.salesCountriesTagsList, it)
                    originsCountriesStr =
                        obtainCountriesString(foodProduct.originsCountriesTagsList, it)
                } else {
                    salesCountriesStr = foodProduct.salesCountriesTagsList?.convertToString()
                    originsCountriesStr = foodProduct.originsCountriesTagsList?.convertToString()
                }

                configureSalesCountries(salesCountriesStr)
                configureOriginsCountries(originsCountriesStr)
            }
        }else{
            viewBinding.fragmentFoodProductOriginsCountriesFrameLayout.visibility = View.GONE
            viewBinding.fragmentFoodProductSalesCountriesFrameLayout.visibility = View.GONE
        }
    }

    private fun obtainCountriesString(countriesTagsList: List<String>?, countriesList: List<Country>): String?{
        return if (!countriesTagsList.isNullOrEmpty()) {
            val countries = mutableListOf<String>()
            for (country in countriesList) {
                if(countriesTagsList.contains(country.tag)){
                    countries.add(country.name)
                }
            }

            countries.sort()
            countries.convertToString()
        } else null
    }
}