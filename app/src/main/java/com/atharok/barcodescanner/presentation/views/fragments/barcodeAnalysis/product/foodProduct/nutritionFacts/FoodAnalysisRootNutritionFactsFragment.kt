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
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisRootNutritionFactsBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient
import com.atharok.barcodescanner.domain.entity.product.foodProduct.NutritionFactsEnum
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisRootNutritionFactsFragment : BarcodeAnalysisFragment<FoodBarcodeAnalysis>() {

    private var _binding: FragmentFoodAnalysisRootNutritionFactsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisRootNutritionFactsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodBarcodeAnalysis) {
        viewBinding.fragmentFoodAnalysisRootNutritionFactsOuterView.fixAnimateLayoutChangesInNestedScroll()
        configureTable(product)
        configureNutrientLevel(product)
    }

    // ---- Table ----

    private fun configureTable(foodProduct: FoodBarcodeAnalysis){
        viewBinding.fragmentFoodAnalysisRootNutritionFactsTableEntitledLayout.visibility = View.GONE
        if(foodProduct.contains100gValues || foodProduct.containsServingValues) {
            configureTableEntitled()
            configureOffNutritionFactsTableFragment()
            viewBinding.fragmentFoodAnalysisRootNutritionFactsNoInformationTextView.visibility = View.GONE
        }
    }

    private fun configureTableEntitled(){
        viewBinding.fragmentFoodAnalysisRootNutritionFactsTableEntitledLayout.visibility = View.VISIBLE
        val tableEntitled: String = getString(R.string.nutrition_facts_entitled_label)
        viewBinding.fragmentFoodAnalysisRootNutritionFactsTableEntitledTextViewTemplate.root.text = tableEntitled
    }

    private fun configureOffNutritionFactsTableFragment() = applyFragment(
        containerViewId = R.id.fragment_food_analysis_root_nutrition_facts_table_frame_layout,
        fragmentClass = FoodAnalysisNutritionFactsTableFragment::class,
        args = arguments
    )

    // ---- Nutrient Level ----


    private fun configureNutrientLevel(foodProduct: FoodBarcodeAnalysis){

        if(foodProduct.containsNutrientLevel){
            configureNutrientLevelEntitled()
            configureFoodProductNutrientLevelFragments(foodProduct.nutrientsList)
        }else{
            viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelEntitledLayout.visibility = View.GONE
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
        containerViewId = viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelFatLayout.id,
        fragment = FoodAnalysisNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSaturatedFatFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelSaturatedFatLayout.id,
        fragment = FoodAnalysisNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSugarsFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelSugarsLayout.id,
        fragment = FoodAnalysisNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureSaltFragment(nutrient: Nutrient) = applyFragment(
        containerViewId = viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelSaltLayout.id,
        fragment = FoodAnalysisNutrientLevelFragment.newInstance(nutrient)
    )

    private fun configureNutrientLevelEntitled(){
        viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelEntitledLayout.visibility = View.VISIBLE
        val nutrientLevelEntitled: String = getString(R.string.nutrient_level_entitled_label)
        viewBinding.fragmentFoodAnalysisRootNutritionFactsNutrientLevelEntitledTextViewTemplate.root.text = nutrientLevelEntitled
    }

    companion object {
        fun newInstance(foodProduct: FoodBarcodeAnalysis) = FoodAnalysisRootNutritionFactsFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, foodProduct)
            }
        }
    }
}
