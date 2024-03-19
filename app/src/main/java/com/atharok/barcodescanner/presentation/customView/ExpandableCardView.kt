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
import com.google.android.material.card.MaterialCardView

class ExpandableCardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
): FrameLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val headerFrameLayout: FrameLayout
    private val bodyFrameLayout: FrameLayout

    private var nbViewsAdded = 0

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.template_expandable_card_view, this, true)

        val materialCardView: MaterialCardView = view.findViewById(R.id.template_expandable_card_view_material_card_view)
        val expandableView: ExpandableView = view.findViewById(R.id.template_expandable_card_view_expandable_view)
        headerFrameLayout = view.findViewById(R.id.template_expandable_card_view_header_frame_layout)
        bodyFrameLayout = view.findViewById(R.id.template_expandable_card_view_body_frame_layout)

        context.theme.obtainStyledAttributes(attrs, R.styleable.ExpandableCardView, defStyleAttr, defStyleRes).apply {
            try {
                getBoolean(R.styleable.ExpandableCardView_isOpen, false).let { isOpen ->
                    if(isOpen) expandableView.open() else expandableView.close()
                }
                getBoolean(R.styleable.ExpandableCardView_cardUseCompatPadding, true).let { cardUseCompatPadding ->
                    materialCardView.useCompatPadding = cardUseCompatPadding
                }
            } finally {
                recycle()
            }
        }
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        when(nbViewsAdded++) {
            0 -> super.addView(child, index, params) // MaterialCardView (root view) from template_expandable_card_view.xml
            1 -> headerFrameLayout.addView(child) // Header
            2 -> bodyFrameLayout.addView(child) // Body
            else -> throw Exception("ExpandableCardView must have two children")
        }
    }
}