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

package com.atharok.barcodescanner.presentation.views.recyclerView.additives

import android.view.LayoutInflater
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.common.extensions.setImageColorFromAttrRes
import com.atharok.barcodescanner.databinding.RecyclerViewItemAdditivesBinding
import com.atharok.barcodescanner.databinding.TemplateChipBinding
import com.atharok.barcodescanner.domain.entity.dependencies.Additive
import com.atharok.barcodescanner.domain.entity.dependencies.AdditiveClass
import com.atharok.barcodescanner.domain.entity.dependencies.OverexposureRiskRate
import com.google.android.material.chip.ChipGroup

/**
 * Représente une ligne (TableRow) d'un tableau (Table) qui est gérer par un Adapter (IngredientsAdapter).
 */
class AdditivesItemHolder(
    private val viewBinding: RecyclerViewItemAdditivesBinding,
    private val showAdditiveInfoDialog: (additiveName: String, description: String) -> Unit,
    private val searchAdditiveOnTheWeb: (additiveId: String) -> Unit
) : RecyclerView.ViewHolder(viewBinding.root) {

    private val context = itemView.context

    fun updateItem(additive: Additive) {

        // ---- Entitled ----
        viewBinding.recyclerViewItemAdditivesEntitledTextView.text = additive.name

        // ---- Info Image Button ----
        viewBinding.recyclerViewItemAdditivesInfoButton.setOnClickListener {
            searchAdditiveOnTheWeb(additive.additiveId)
        }

        // ---- Overexposure Risk ----
        val overexposure = additive.overexposureRiskRate
        viewBinding.recyclerViewItemAdditivesOverexposureRiskImageView.setImageColorFromAttrRes(
            overexposure.colorResource
        )
        viewBinding.recyclerViewItemAdditivesOverexposureRiskTextView.text = context.getString(
            overexposure.stringResource
        )

        if(overexposure == OverexposureRiskRate.NONE){
            viewBinding.recyclerViewItemAdditivesOverexposureRiskLayout.visibility = View.GONE
        }

        handleType(additive.additiveClassList)
    }

    private fun handleType(additiveClassList: List<AdditiveClass>) {
        val chipGroup: ChipGroup = viewBinding.recyclerViewItemAdditivesTypeChipGroup
        if(additiveClassList.isNotEmpty()) {
            val inflater = LayoutInflater.from(context)
            for (additiveClass in additiveClassList) {
                val chipLayout = TemplateChipBinding.inflate(inflater)
                chipLayout.root.apply {
                    this.text = additiveClass.name
                    this.setOnClickListener {
                        showAdditiveInfoDialog(additiveClass.name, additiveClass.description)
                    }
                    chipGroup.addView(this)
                }
            }
        } else {
            chipGroup.visibility = View.GONE
        }
    }
}