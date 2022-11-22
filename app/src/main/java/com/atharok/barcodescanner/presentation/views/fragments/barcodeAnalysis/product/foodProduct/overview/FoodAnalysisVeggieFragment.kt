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

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.*
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisVeggieBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.presentation.views.activities.VeggieActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

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

    override fun start(product: FoodBarcodeAnalysis) {
        configureChipGroup(product)

        val onClickListener: View.OnClickListener = getVeggieOnClickListener(product)
        viewBinding.fragmentFoodAnalysisVeggieVeganChip.setOnClickListener(onClickListener)
        viewBinding.fragmentFoodAnalysisVeggieVegetarianChip.setOnClickListener(onClickListener)
    }

    private fun configureChipGroup(foodProduct: FoodBarcodeAnalysis){

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.fragmentFoodAnalysisVeggieVeganChip,
            colorResource = foodProduct.veganStatus.colorResource,
            drawableResource = foodProduct.veganStatus.drawableResource,
            stringResource = foodProduct.veganStatus.stringResource
        )

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.fragmentFoodAnalysisVeggieVegetarianChip,
            colorResource = foodProduct.vegetarianStatus.colorResource,
            drawableResource = foodProduct.vegetarianStatus.drawableResource,
            stringResource = foodProduct.vegetarianStatus.stringResource
        )

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.fragmentFoodAnalysisVeggiePalmOilChip,
            colorResource = foodProduct.palmOilStatus.colorResource,
            drawableResource = foodProduct.palmOilStatus.drawableResource,
            stringResource = foodProduct.palmOilStatus.stringResource
        )
    }

    private fun configureChipWithIngredientsAnalysis(chipView: Chip, @AttrRes colorResource: Int, drawableResource: Int, stringResource: Int){

        chipView.text = getString(stringResource).firstCharacterIntoCapital()

        chipView.setChipTextColorFromAttrRes(colorResource)
        val backgroundRes = if(get<SettingsManager>().useDarkTheme()) R.attr.appBackgroundColorPrimary else R.attr.appBackgroundColorSecondary
        chipView.setChipBackgroundColorFromAttrRes(backgroundRes)
        chipView.setChipStrokeColorFromAttrRes(colorResource)
        chipView.setChipIconTintFromAttrRes(colorResource)
        chipView.setChipIconResource(drawableResource)
        chipView.isChipIconVisible = true
    }

    private fun getVeggieOnClickListener(foodProduct: FoodBarcodeAnalysis) = View.OnClickListener {
        val intent = getVeggieActivityIntent().apply {
            putExtra(PRODUCT_KEY, foodProduct)
        }

        startActivity(intent)
    }

    private fun getVeggieActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(VeggieActivity::class) }
}