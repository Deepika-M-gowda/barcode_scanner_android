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

import android.graphics.Typeface
import android.widget.TextView
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.atharok.barcodescanner.R

fun Preference.initializeCustomResourcesValues(holder: PreferenceViewHolder?) {

    //val font: Typeface? = ResourcesCompat.getFont(context, R.font.roboto_medium)
    val primaryTextSizeInDP = context.resources.getDimension(R.dimen.sub_title_text_size) / context.resources.displayMetrics.density

    val titleView: TextView? = holder?.itemView?.findViewById(android.R.id.title)
    titleView?.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)//Typeface.SANS_SERIF
    titleView?.textSize = primaryTextSizeInDP

    val secondaryTextSizeInDP = context.resources.getDimension(R.dimen.standard_text_size) / context.resources.displayMetrics.density
    val summaryView: TextView? = holder?.itemView?.findViewById(android.R.id.summary)
    summaryView?.typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)//Typeface.SANS_SERIF
    summaryView?.textSize = secondaryTextSizeInDP
}