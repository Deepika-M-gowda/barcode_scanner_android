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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.foodProduct.nutritionFacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.extensions.setImageColorFromAttrRes
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisNutrientLevelBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisNutrientLevelFragment : Fragment() {

    private var nutrient: Nutrient? = null

    private var _binding: FragmentFoodAnalysisNutrientLevelBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nutrient = it.serializable(NUTRIENT_KEY, Nutrient::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisNutrientLevelBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureSubView(nutrient)
    }

    private fun configureSubView(nutrient: Nutrient?) {

        if(nutrient != null) {
            // Entitled
            val entitled: String = getString(nutrient.entitled.stringResource)
            viewBinding.fragmentFoodAnalysisNutrientLevelTitleTextView.text = entitled

            // Subtitle
            val subtitle: String = getString(nutrient.getQuantityValue().stringResource)
            viewBinding.fragmentFoodAnalysisNutrientLevelSubtitleTextView.text = subtitle

            // Weight
            val weight: String = nutrient.values.getValue100gString()
            viewBinding.fragmentFoodAnalysisNutrientLevelQuantityTextView.text = weight

            // Indicator Image
            @AttrRes val colorRes: Int = nutrient.getQuantityValue().colorResource
            viewBinding.fragmentFoodAnalysisNutrientLevelIndicatorImageView.setImageColorFromAttrRes(colorRes)

            // GraphView
            configureGraphView(nutrient)
        } else {
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureGraphView(nutrient: Nutrient) {

        val currentQuantity: Float? = nutrient.values.value100g?.toFloat()
        val lowQuantity: Float? = nutrient.getLowQuantity()
        val highQuantity: Float? = nutrient.getHighQuantity()

        if(currentQuantity!=null && lowQuantity!=null && highQuantity!=null) {

            viewBinding.fragmentFoodAnalysisNutrientLevelHorizontalGraphView.setValues(
                guidePosition = currentQuantity,
                low = lowQuantity,
                high = highQuantity
            )
        } else {
            viewBinding.fragmentFoodAnalysisNutrientLevelHorizontalGraphView.visibility = View.GONE
        }
    }

    companion object {
        private const val NUTRIENT_KEY = "nutrientKey"

        @JvmStatic
        fun newInstance(nutrient: Nutrient) =
            FoodAnalysisNutrientLevelFragment().apply {
                arguments = get<Bundle>().apply {
                    putSerializable(NUTRIENT_KEY, nutrient)
                }
            }
    }
}