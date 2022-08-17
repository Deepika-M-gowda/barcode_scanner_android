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

package com.atharok.barcodescanner.presentation.views.recyclerView.nutritionFacts

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.RecyclerViewItemNutritionFactsBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient
import com.atharok.barcodescanner.domain.entity.product.foodProduct.NutritionFactsEnum
import com.atharok.barcodescanner.common.extensions.setBackground

class NutritionFactsHolder(private val viewBinding: RecyclerViewItemNutritionFactsBinding): RecyclerView.ViewHolder(viewBinding.root) {

    private val context = itemView.context

    fun updateItem(nutrient: Nutrient, showServing: Boolean) {

        // Pour les sous-nutriments
        if(nutrient.entitled == NutritionFactsEnum.SATURATED_FAT ||
            nutrient.entitled == NutritionFactsEnum.SUGARS ||
            nutrient.entitled == NutritionFactsEnum.STARCH ||
            nutrient.entitled == NutritionFactsEnum.SODIUM){

            viewBinding.root.setBackground(R.attr.background_secondary_row)
            val leftPadding = context.resources.getDimension(R.dimen.standard_margin).toInt()
            viewBinding.recyclerViewItemNutritionFactsEntitledTextView.setPadding(leftPadding,
                viewBinding.recyclerViewItemNutritionFactsEntitledTextView.paddingTop,
                viewBinding.recyclerViewItemNutritionFactsEntitledTextView.paddingRight,
                viewBinding.recyclerViewItemNutritionFactsEntitledTextView.paddingBottom)
        }

        viewBinding.recyclerViewItemNutritionFactsEntitledTextView.text = context.getString(nutrient.entitled.stringResource)

        viewBinding.recyclerViewItemNutritionFacts100gValueTextView.text = nutrient.values.getValue100gString()

        if (showServing)
            viewBinding.recyclerViewItemNutritionFactsServingValueTextView.text = nutrient.values.getValueServingString()
        else
            viewBinding.recyclerViewItemNutritionFactsServingValueTextView.visibility = View.GONE
    }
}