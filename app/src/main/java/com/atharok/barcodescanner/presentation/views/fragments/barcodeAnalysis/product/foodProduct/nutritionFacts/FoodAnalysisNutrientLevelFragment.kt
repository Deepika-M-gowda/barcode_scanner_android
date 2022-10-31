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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import com.atharok.barcodescanner.databinding.TemplateHorizontalGraphViewBinding
import com.atharok.barcodescanner.databinding.TemplateNutrientLevelBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient
import com.atharok.barcodescanner.common.extensions.setImageColorFromAttrRes
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisNutrientLevelBinding
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisNutrientLevelFragment : Fragment() {

    private var nutrient: Nutrient? = null

    private var _binding: FragmentFoodAnalysisNutrientLevelBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerNutrientLevelTemplateBinding: TemplateNutrientLevelBinding
    private lateinit var bodyGraphViewTemplateBinding: TemplateHorizontalGraphViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            nutrient = it.getSerializable(NUTRIENT_KEY, Nutrient::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisNutrientLevelBinding.inflate(inflater, container, false)
        configureNutrientLevelTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureNutrientLevelTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentFoodAnalysisNutrientLevelExpandableViewTemplate

        expandableViewTemplate.root.close() // L'ExpandableView est fermée par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody =  expandableViewTemplate.templateExpandableViewBodyFrameLayout

        headerNutrientLevelTemplateBinding = TemplateNutrientLevelBinding.inflate(inflater, parentHeader, true)
        bodyGraphViewTemplateBinding = TemplateHorizontalGraphViewBinding.inflate(inflater, parentBody, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureSubView(nutrient)
    }

    private fun configureSubView(nutrient: Nutrient?){

        if(nutrient != null) {
            // Entitled
            val entitled: String = getString(nutrient.entitled.stringResource)
            headerNutrientLevelTemplateBinding.templateNutrientLevelEntitledTextView.text = entitled

            // Subtitle
            val subtitle: String = getString(nutrient.getQuantityValue().stringResource)
            headerNutrientLevelTemplateBinding.templateNutrientLevelSubEntitledTextView.text =
                subtitle

            // Weight
            val weight: String = nutrient.values.getValue100gString()
            headerNutrientLevelTemplateBinding.templateNutrientLevelQuantityTextView.text = weight

            // Indicator Image
            @AttrRes val colorRes: Int = nutrient.getQuantityValue().colorResource
            headerNutrientLevelTemplateBinding
                .templateNutrientLevelIndicatorImageView.setImageColorFromAttrRes(colorRes)

            // GraphView
            configureGraphView(nutrient)
        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureGraphView(nutrient: Nutrient) {

        val currentQuantity: Float? = nutrient.values.value100g?.toFloat()
        val lowQuantity: Float? = nutrient.getLowQuantity()
        val highQuantity: Float? = nutrient.getHighQuantity()

        if(currentQuantity!=null && lowQuantity!=null && highQuantity!=null) {

            bodyGraphViewTemplateBinding.root.setValues(
                guidePosition = currentQuantity,
                low = lowQuantity,
                high = highQuantity
            )
        } else {
            bodyGraphViewTemplateBinding.root.visibility = View.GONE
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