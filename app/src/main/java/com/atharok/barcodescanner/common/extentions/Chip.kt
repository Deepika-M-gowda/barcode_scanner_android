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

package com.atharok.barcodescanner.common.extentions

import android.content.res.ColorStateList
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import com.google.android.material.chip.Chip

fun Chip.setChipBackgroundColorFromAttrRes(@AttrRes attrRes: Int){
    @ColorInt val color = convertAttrResToColorInt(attrRes)
    this.chipBackgroundColor = ColorStateList.valueOf(color)
}

fun Chip.setChipTextColorFromAttrRes(@AttrRes attrRes: Int){
    @ColorInt val color = convertAttrResToColorInt(attrRes)
    setTextColor(ColorStateList.valueOf(color))
}

fun Chip.setChipStrokeColorFromAttrRes(@AttrRes attrRes: Int){
    @ColorInt val color = convertAttrResToColorInt(attrRes)
    chipStrokeColor = ColorStateList.valueOf(color)
}

fun Chip.setChipIconTintFromAttrRes(@AttrRes attrRes: Int){
    @ColorInt val color = convertAttrResToColorInt(attrRes)
    chipIconTint = ColorStateList.valueOf(color)
}