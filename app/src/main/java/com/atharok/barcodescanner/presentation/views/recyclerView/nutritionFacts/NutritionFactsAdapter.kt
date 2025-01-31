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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemNutritionFactsBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutrient

class NutritionFactsAdapter(private val nutrientsList: List<Nutrient>, private val showServing: Boolean): RecyclerView.Adapter<NutritionFactsHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NutritionFactsHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewBinding = RecyclerViewItemNutritionFactsBinding.inflate(layoutInflater, parent, false)

        return NutritionFactsHolder(viewBinding)
    }

    override fun getItemCount(): Int = nutrientsList.size

    override fun onBindViewHolder(holder: NutritionFactsHolder, position: Int) {
        holder.updateItem(nutrientsList[position], showServing)
    }
}