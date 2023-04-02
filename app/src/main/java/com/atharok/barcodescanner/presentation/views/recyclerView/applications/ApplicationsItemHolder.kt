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

package com.atharok.barcodescanner.presentation.views.recyclerView.applications

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.databinding.RecyclerViewItemApplicationsBinding
import java.lang.ref.WeakReference

/**
 * Représente une ligne (TableRow) d'un tableau (Table) qui est gérer par un Adapter (IngredientsAdapter).
 */
class ApplicationsItemHolder(private val viewBinding: RecyclerViewItemApplicationsBinding)
    : RecyclerView.ViewHolder(viewBinding.root), View.OnClickListener {

    private var weakRefCallback: WeakReference<ApplicationsItemAdapter.OnApplicationItemListener>? = null
    private var item: ApplicationsItem? = null

    init {
        itemView.rootView.apply {
            setOnClickListener(this@ApplicationsItemHolder)
        }
    }

    fun updateItem(applicationsItem: ApplicationsItem, listener: ApplicationsItemAdapter.OnApplicationItemListener) {
        viewBinding.recyclerViewItemApplicationsTitleTextView.text = applicationsItem.title
        viewBinding.recyclerViewItemApplicationsPkgTextView.text = applicationsItem.pkg
        viewBinding.recyclerViewItemApplicationsImageView.setImageDrawable(applicationsItem.drawable)

        this.weakRefCallback = WeakReference(listener)
        this.item = applicationsItem
    }

    override fun onClick(v: View?) {
        item?.let {
            this.weakRefCallback?.get()?.onItemClick(v, it)
        }
    }
}