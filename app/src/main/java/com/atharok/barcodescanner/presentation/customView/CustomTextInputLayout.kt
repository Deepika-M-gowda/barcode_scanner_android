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

package com.atharok.barcodescanner.presentation.customView

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.children
import com.atharok.barcodescanner.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 * Redéfinition permettant de changer la couleur de l'icone (app:startIconDrawable) lorsque le TextInputEditText enfant a le focus.
 */
class CustomTextInputLayout(context: Context, attrs: AttributeSet?): TextInputLayout(context, attrs){

    private val startIconTint: Int?
    private val startIconTintFocused: Int?

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout, 0, 0).apply {
            try {
                startIconTint = getColorStateList(R.styleable.CustomTextInputLayout_startIconTint)?.defaultColor
                startIconTintFocused = getColorStateList(R.styleable.CustomTextInputLayout_startIconTintFocused)?.defaultColor
            } finally {
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        setStartIconTintList(obtainColorStateList(startIconTint))

        if(childCount>0) {

            val subView = children.elementAt(0) // FrameLayout

            if(subView is FrameLayout){

                subView.children.forEach {

                    if(it is TextInputEditText) {
                        it.setOnFocusChangeListener { _, hasFocus ->

                            val colorStateList =
                                if (hasFocus)
                                    obtainColorStateList(startIconTintFocused)
                                else
                                    obtainColorStateList(startIconTint)

                            setStartIconTintList(colorStateList)
                        }
                    }
                }
            }
        }
    }

    private fun obtainColorStateList(resColor: Int?): ColorStateList? = if(resColor!=null) ColorStateList.valueOf(resColor) else null
}