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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.nutritionFacts

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.atharok.barcodescanner.databinding.FragmentFoodProductNutritionFactsTableBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.presentation.views.recyclerView.nutritionFacts.NutritionFactsAdapter

/**
 * A simple [Fragment] subclass.
 */
class FoodProductNutritionFactsTableFragment : ProductBarcodeFragment<FoodProduct>() {

    private var _binding: FragmentFoodProductNutritionFactsTableBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodProductNutritionFactsTableBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodProduct) {
        configureHeaderRowTable(product)
        configureRecyclerView(product)
    }

    private fun configureHeaderRowTable(foodProduct: FoodProduct){
        // Entitled
        viewBinding.nutritionFactsTableEntitled100TextView.text =
            getString(R.string.off_per_100_label, foodProduct.unit)

        if(!foodProduct.containsServingValues){
            viewBinding.nutritionFactsTableEntitledServingTextView.visibility = View.GONE
        }
        else {
            viewBinding.nutritionFactsTableEntitledServingTextView.text =
                getString(R.string.off_per_serving_label, foodProduct.servingQuantity.toString(), foodProduct.unit)
        }

    }

    private fun configureRecyclerView(foodProduct: FoodProduct){
        viewBinding.nutritionFactsTableRecyclerView.adapter = NutritionFactsAdapter(foodProduct.nutrientsList, foodProduct.containsServingValues)
        viewBinding.nutritionFactsTableRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        viewBinding.nutritionFactsTableRecyclerView.suppressLayout(true)
    }
}