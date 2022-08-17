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

package com.atharok.barcodescanner.common.extensions

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

fun View.setBackground(@AttrRes attrRes: Int) {
    val attrs = intArrayOf(attrRes)
    val typedArray = context.obtainStyledAttributes(attrs)

    val drawable: Drawable? = typedArray.getDrawable(0)

    if(drawable!=null)
        this.background = drawable

    typedArray.recycle()
}

@ColorInt
fun View.convertAttrResToColorInt(@AttrRes attrRes: Int): Int {

    val typedValue = TypedValue()
    val theme = context.theme
    theme.resolveAttribute(attrRes, typedValue, true)

    return typedValue.data
}