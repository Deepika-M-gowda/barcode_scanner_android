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

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.setImageColorFromAttrRes
import com.atharok.barcodescanner.databinding.RecyclerViewItemAdditivesBinding
import com.atharok.barcodescanner.domain.entity.dependencies.Additive
import com.atharok.barcodescanner.domain.entity.dependencies.AdditiveClass
import com.atharok.barcodescanner.domain.entity.dependencies.OverexposureRiskRate
import com.atharok.barcodescanner.presentation.intent.createSearchUrlIntent
import com.google.android.material.chip.Chip
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

/**
 * Représente une ligne (TableRow) d'un tableau (Table) qui est gérer par un Adapter (IngredientsAdapter).
 */
class AdditivesItemHolder(private val activity: Activity,
                          private val viewBinding: RecyclerViewItemAdditivesBinding)
    : RecyclerView.ViewHolder(viewBinding.root) {

    private val context = itemView.context

    fun updateItem(additive: Additive) {

        // ---- Entitled ----
        viewBinding.recyclerViewItemAdditivesEntitledTextView.text = additive.name

        // ---- Info Image Button ----
        viewBinding.recyclerViewItemAdditivesInfoButton.setOnClickListener {
            val url = context.getString(R.string.search_engine_additive_url, additive.additiveId)
            val intent: Intent = createSearchUrlIntent(url)
            activity.startActivity(intent)
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

    private fun handleType(additiveClassList: List<AdditiveClass>){

        if(additiveClassList.isNotEmpty()) {
            for (additiveClass in additiveClassList) {
                val chip = activity.get<Chip> { parametersOf(activity, additiveClass.name) }

                viewBinding.recyclerViewItemAdditivesTypeLayout.addView(chip)

                chip.setOnClickListener {
                    showAdditiveClassDescriptionDialog(additiveClass.name, additiveClass.description)
                }
            }
        }else{
            viewBinding.recyclerViewItemAdditivesTypeLayout.visibility = View.GONE
        }

    }

    private fun showAdditiveClassDescriptionDialog(title: String, message: String){
        val dialog = activity.get<AlertDialog> {
            parametersOf(activity, title, message)
        }

        dialog.show()
    }
}