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
 *//*
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

package com.atharok.barcodescanner.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.atharok.barcodescanner.R
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

class ActivityLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    val appBarLayout: AppBarLayout
    val toolbar: MaterialToolbar
    val scrollContent: FrameLayout

    private var nbViewsAdded = 0

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.template_activity_layout, this, true)

        appBarLayout = view.findViewById(R.id.app_bar_layout)
        toolbar = view.findViewById(R.id.toolbar)
        scrollContent = view.findViewById(R.id.scroll_content)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        when(nbViewsAdded++) {
            0 -> super.addView(child, index, params) // CoordinatorLayout (root view) from template_activity_layout.xml
            1 -> scrollContent.addView(child) // scroll content
            else -> throw Exception("ActivityLayout must have one child")
        }
    }
}