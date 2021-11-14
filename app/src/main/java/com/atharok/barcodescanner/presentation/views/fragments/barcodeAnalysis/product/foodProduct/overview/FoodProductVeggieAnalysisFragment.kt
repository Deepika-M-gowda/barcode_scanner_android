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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import com.atharok.barcodescanner.databinding.FragmentFoodProductVeggieAnalysisBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.common.extentions.firstCharacterIntoCapital
import com.atharok.barcodescanner.common.extentions.setChipIconTintFromAttrRes
import com.atharok.barcodescanner.common.extentions.setChipStrokeColorFromAttrRes
import com.atharok.barcodescanner.common.extentions.setChipTextColorFromAttrRes
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.presentation.views.activities.VeggieActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.google.android.material.chip.Chip

/**
 * A simple [Fragment] subclass.
 */
class FoodProductVeggieAnalysisFragment: ProductBarcodeFragment<FoodProduct>() {

    private var _binding: FragmentFoodProductVeggieAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodProductVeggieAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodProduct) {
        configureChipGroup(product)

        val onClickListener: View.OnClickListener = getVeggieOnClickListener(product)
        viewBinding.foodProductOverviewVeganChip.setOnClickListener(onClickListener)
        viewBinding.foodProductOverviewVegetarianChip.setOnClickListener(onClickListener)
    }

    private fun configureChipGroup(foodProduct: FoodProduct){

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.foodProductOverviewVeganChip,
            colorResource = foodProduct.veganStatus.colorResource,
            drawableResource = foodProduct.veganStatus.drawableResource,
            stringResource = foodProduct.veganStatus.stringResource)

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.foodProductOverviewVegetarianChip,
            colorResource = foodProduct.vegetarianStatus.colorResource,
            drawableResource = foodProduct.vegetarianStatus.drawableResource,
            stringResource = foodProduct.vegetarianStatus.stringResource)

        configureChipWithIngredientsAnalysis(
            chipView = viewBinding.foodProductOverviewPalmOilChip,
            colorResource = foodProduct.palmOilStatus.colorResource,
            drawableResource = foodProduct.palmOilStatus.drawableResource,
            stringResource = foodProduct.palmOilStatus.stringResource)

    }

    private fun configureChipWithIngredientsAnalysis(chipView: Chip, @AttrRes colorResource: Int, drawableResource: Int, stringResource: Int){

        chipView.text = getString(stringResource).firstCharacterIntoCapital()

        chipView.setChipTextColorFromAttrRes(colorResource)
        chipView.setChipBackgroundColorResource(android.R.color.transparent)
        chipView.setChipStrokeColorFromAttrRes(colorResource)
        chipView.setChipIconTintFromAttrRes(colorResource)
        chipView.setChipIconResource(drawableResource)
        chipView.isChipIconVisible = true
    }

    private fun getVeggieOnClickListener(foodProduct: FoodProduct) = View.OnClickListener {
        val intent = Intent(requireContext(), VeggieActivity::class.java).apply {
            putExtra(PRODUCT_KEY, foodProduct)
        }

        startActivity(intent)
    }
}