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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutPermissions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemAboutBinding

class PermissionsDescriptionItemAdapter: RecyclerView.Adapter<PermissionsDescriptionItemHolder>() {

    private val permissionsDescriptionArray: Array<PermissionsDescription> = PermissionsDescription.entries.toTypedArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PermissionsDescriptionItemHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val viewBinding = RecyclerViewItemAboutBinding.inflate(layoutInflater, parent, false)

        return PermissionsDescriptionItemHolder(viewBinding)
    }

    override fun getItemCount(): Int = permissionsDescriptionArray.size

    override fun onBindViewHolder(holder: PermissionsDescriptionItemHolder, position: Int) {
        holder.updateItem(permissionsDescriptionArray[position])
    }
}