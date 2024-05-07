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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutThirdPartyLibrary

import android.content.Intent
import android.graphics.Paint
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.RecyclerViewItemAboutThirdPartyLibrariesBinding
import com.atharok.barcodescanner.presentation.intent.createSearchUrlIntent

class ThirdPartyLibraryItemHolder(
    private val viewBinding: RecyclerViewItemAboutThirdPartyLibrariesBinding
) : RecyclerView.ViewHolder(viewBinding.root) {

    private val context = itemView.context

    fun updateItem(thirdPartyLibrary: ThirdPartyLibrary) {
        viewBinding.recyclerViewItemAboutThirdPartyLibrariesTitleTextView.text = context.getString(thirdPartyLibrary.title)
        viewBinding.recyclerViewItemAboutThirdPartyLibrariesAuthorTextView.text = context.getString(R.string.dependency_by, context.getString(thirdPartyLibrary.author))
        viewBinding.recyclerViewItemAboutThirdPartyLibrariesIdTextView.text = context.getString(thirdPartyLibrary.id)

        viewBinding.recyclerViewItemAboutThirdPartyLibrariesSourceCodeTextView.apply {
            text = context.getString(thirdPartyLibrary.sourceCode)
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                val url = context.getString(thirdPartyLibrary.sourceCodeUrl)
                val intent: Intent = createSearchUrlIntent(url)
                context.startActivity(intent)
            }
        }

        viewBinding.recyclerViewItemAboutThirdPartyLibrariesLicenseTextView.apply {
            text = context.getString(thirdPartyLibrary.license)
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setOnClickListener {
                val url = context.getString(thirdPartyLibrary.licenseUrl)
                val intent: Intent = createSearchUrlIntent(url)
                context.startActivity(intent)
            }
        }
    }
}