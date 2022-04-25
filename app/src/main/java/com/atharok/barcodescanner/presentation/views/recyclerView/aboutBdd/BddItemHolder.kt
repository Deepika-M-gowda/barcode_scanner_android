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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutBdd

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.common.utils.INTENT_SEARCH_URL
import com.atharok.barcodescanner.databinding.RecyclerViewItemAboutBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class BddItemHolder(private val viewBinding: RecyclerViewItemAboutBinding)
    : RecyclerView.ViewHolder(viewBinding.root), KoinComponent {

    private val context = itemView.context

    fun updateItem(bdd: Bdd) {

        viewBinding.recyclerViewItemAboutTitleTextView.text = context.getString(bdd.nameResource)
        viewBinding.recyclerViewItemAboutLicenseTextView.visibility = View.GONE
        viewBinding.recyclerViewItemAboutAuthorTextView.visibility = View.GONE
        viewBinding.recyclerViewItemAboutDescriptionTextView.text = context.getString(bdd.descriptionResource)

        itemView.setOnClickListener {
            val url = context.getString(bdd.webLinkResource)
            val intent: Intent = get(named(INTENT_SEARCH_URL)) { parametersOf(url) }
            context.startActivity(intent)
        }
    }
}