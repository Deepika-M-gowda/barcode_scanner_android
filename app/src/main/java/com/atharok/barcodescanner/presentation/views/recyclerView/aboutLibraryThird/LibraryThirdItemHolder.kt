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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutLibraryThird

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.RecyclerViewItemAboutBinding

class LibraryThirdItemHolder(private val viewBinding: RecyclerViewItemAboutBinding)
    : RecyclerView.ViewHolder(viewBinding.root) {

    private val context = itemView.context

    fun updateItem(libraryThird: LibraryThird) {

        viewBinding.recyclerViewItemAboutTitleTextView.text = context.getString(libraryThird.nameResource)
        viewBinding.recyclerViewItemAboutLicenseTextView.text = context.getString(libraryThird.licenseResource)
        viewBinding.recyclerViewItemAboutAuthorTextView.text = context.getString(R.string.dependency_by, context.getString(libraryThird.authorResource))
        viewBinding.recyclerViewItemAboutDescriptionTextView.text = context.getString(libraryThird.descriptionResource)

        itemView.setOnClickListener {
            val url = context.getString(libraryThird.webLinkResource)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }
}