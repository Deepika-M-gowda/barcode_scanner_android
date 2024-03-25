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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.foodProduct.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extensions.getColorInt
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_KEY
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisVeggieBinding
import com.atharok.barcodescanner.domain.entity.analysis.FoodBarcodeAnalysis
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.views.activities.VeggieActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.BarcodeAnalysisFragment

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisVeggieFragment: BarcodeAnalysisFragment<FoodBarcodeAnalysis>() {

    private var _binding: FragmentFoodAnalysisVeggieBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisVeggieBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(analysis: FoodBarcodeAnalysis) {

        val onClickListener: View.OnClickListener = getVeggieOnClickListener(analysis)

        configureAnalysis(
            view = viewBinding.fragmentFoodAnalysisVeggieVegan.root,
            checkIconView = viewBinding.fragmentFoodAnalysisVeggieVegan.templateIngredientsAnalysisCheckIcon,
            textView = viewBinding.fragmentFoodAnalysisVeggieVegan.templateIngredientsAnalysisTextView,
            colorResource = analysis.veganStatus.colorResource,
            stringResource = analysis.veganStatus.stringResource,
            onClickListener = onClickListener
        )

        configureAnalysis(
            view = viewBinding.fragmentFoodAnalysisVeggieVegetarian.root,
            checkIconView = viewBinding.fragmentFoodAnalysisVeggieVegetarian.templateIngredientsAnalysisCheckIcon,
            textView = viewBinding.fragmentFoodAnalysisVeggieVegetarian.templateIngredientsAnalysisTextView,
            colorResource = analysis.vegetarianStatus.colorResource,
            stringResource = analysis.vegetarianStatus.stringResource,
            onClickListener = onClickListener
        )

        configureAnalysis(
            view = viewBinding.fragmentFoodAnalysisVeggiePalmOil.root,
            checkIconView = viewBinding.fragmentFoodAnalysisVeggiePalmOil.templateIngredientsAnalysisCheckIcon,
            textView = viewBinding.fragmentFoodAnalysisVeggiePalmOil.templateIngredientsAnalysisTextView,
            colorResource = analysis.palmOilStatus.colorResource,
            stringResource = analysis.palmOilStatus.stringResource
        )
        viewBinding.fragmentFoodAnalysisVeggiePalmOil.templateIngredientsAnalysisEndIcon.visibility = View.INVISIBLE
    }

    private fun configureAnalysis(
        view: View,
        checkIconView: ImageView,
        textView: TextView,
        @AttrRes colorResource: Int,
        @StringRes stringResource: Int,
        onClickListener: View.OnClickListener? = null
    ) {
        textView.setText(stringResource)
        checkIconView.setColorFilter(requireContext().getColorInt(colorResource))
        onClickListener?.let { view.setOnClickListener(it) }
    }

    private fun getVeggieOnClickListener(foodProduct: FoodBarcodeAnalysis) = View.OnClickListener {
        val intent = createStartActivityIntent(requireContext(), VeggieActivity::class).apply {
            putExtra(BARCODE_ANALYSIS_KEY, foodProduct)
        }
        startActivity(intent)
    }
}