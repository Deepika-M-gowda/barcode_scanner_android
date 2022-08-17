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
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.atharok.barcodescanner.databinding.FragmentFoodProductRootNutritionFactsBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient
import com.atharok.barcodescanner.domain.entity.product.foodProduct.NutritionFactsEnum
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodProductRootNutritionFactsFragment : ProductBarcodeFragment<FoodProduct>() {

    private var _binding: FragmentFoodProductRootNutritionFactsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodProductRootNutritionFactsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodProduct) {
        viewBinding.rootNutritionFactsOuterView.fixAnimateLayoutChangesInNestedScroll()
        configureTable(product)
        configureNutrientLevel(product)
    }

    // ---- Table ----

    private fun configureTable(foodProduct: FoodProduct){
        viewBinding.rootNutritionFactsTableEntitledLayout.visibility = View.GONE
        if(foodProduct.contains100gValues || foodProduct.containsServingValues) {
            configureTableEntitled()
            configureOffNutritionFactsTableFragment()
            viewBinding.rootNutritionFactsNoInformationTextView.visibility = View.GONE
        }
    }

    private fun configureTableEntitled(){
        viewBinding.rootNutritionFactsTableEntitledLayout.visibility = View.VISIBLE
        val tableEntitled: String = getString(R.string.nutrition_facts_entitled_label)
        viewBinding.rootNutritionFactsTableEntitledTextViewTemplate.root.text = tableEntitled
    }

    private fun configureOffNutritionFactsTableFragment() = applyFragment(
        containerViewId = R.id.root_nutrition_facts_table_frame_layout,
        fragmentClass = FoodProductNutritionFactsTableFragment::class,
        args = arguments
    )

    // ---- Nutrient Level ----


    private fun configureNutrientLevel(foodProduct: FoodProduct){

        if(foodProduct.containsNutrientLevel){
            configureNutrientLevelEntitled()
            configureFoodProductNutrientLevelFragments(foodProduct.nutrientsList)
        }else{
            viewBinding.rootNutritionFactsNutrientLevelEntitledLayout.visibility = View.GONE
        }
    }

    private fun configureFoodProductNutrientLevelFragments(nutrientsList: List<Nutrient>){

        nutrientsList.forEach {
            when (it.entitled) {
                NutritionFactsEnum.FAT -> configureFatFragment(it)
                NutritionFactsEnum.SATURATED_FAT -> configureSaturatedFatFragment(it)
                NutritionFactsEnum.SUGARS -> configureSugarsFragment(it)
                NutritionFactsEnum.SALT -> configureSaltFragment(it)
                else -> {}
            }
        }
    }

    private fun configureFatFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodProductNutrientLevelFatLayout.id,
        fragment = FoodProductNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSaturatedFatFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodProductNutrientLevelSaturatedFatLayout.id,
        fragment = FoodProductNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSugarsFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodProductNutrientLevelSugarsLayout.id,
        fragment = FoodProductNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSaltFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodProductNutrientLevelSaltLayout.id,
        fragment = FoodProductNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureNutrientLevelEntitled(){
        viewBinding.rootNutritionFactsNutrientLevelEntitledLayout.visibility = View.VISIBLE
        val nutrientLevelEntitled: String = getString(R.string.nutrient_level_entitled_label)
        viewBinding.rootNutritionFactsNutrientLevelEntitledTextViewTemplate.root.text = nutrientLevelEntitled
    }

    companion object {
        fun newInstance(foodProduct: FoodProduct) = FoodProductRootNutritionFactsFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, foodProduct)
            }
        }
    }
}
