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

package com.atharok.barcodescanner.domain.library

import android.content.Context
import android.text.format.DateFormat
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat

/**
 * Convertit une date au format "yyyy-MM-dd" dans le format de date correspondant à la langue de l'appareil.
 */
class DateConverter: KoinComponent {
    fun convertDateToLocalizedFormat(context: Context, dateString: String?, format: String = "yyyy-MM-dd"): String? {
        if(dateString != null) {
            val inputFormat: SimpleDateFormat = get { parametersOf(format) }
            try {
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    val outputFormat = DateFormat.getDateFormat(context)
                    return outputFormat.format(date)
                }
            } catch (e: Exception) {
                return dateString
            }
        }
        return dateString
    }
}