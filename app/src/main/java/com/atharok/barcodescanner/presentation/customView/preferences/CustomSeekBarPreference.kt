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

package com.atharok.barcodescanner.presentation.customView.preferences

import android.content.Context
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.getColorStateListFromAttrRes
import com.atharok.barcodescanner.common.extensions.initializeCustomResourcesValues

class CustomSeekBarPreference: SeekBarPreference {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        initializeCustomResourcesValues(holder)

        val seekBar = holder.findViewById(R.id.seekbar) as SeekBar
        seekBar.thumbTintList = context.getColorStateListFromAttrRes(android.R.attr.colorPrimary)
        seekBar.progressTintList = context.getColorStateListFromAttrRes(android.R.attr.colorPrimary)
        //seekBar.progressBackgroundTintList = context.getColorStateListFromAttrRes(android.R.attr.colorButtonNormal)
    }
}