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

import java.lang.StringBuilder

class VEventBuilder {

    companion object{
        private const val BEGIN_VEVENT = "BEGIN:VEVENT"
        private const val END_VEVENT = "END:VEVENT"

        private const val SUMMARY = "SUMMARY:"
        private const val DTSTART = "DTSTART:"
        private const val DTEND = "DTEND:"
        private const val LOCATION = "LOCATION:"
        private const val DESCRIPTION = "DESCRIPTION:"
    }

    private val stringBuilder: StringBuilder = StringBuilder()

    init {
        stringBuilder.clear()

        stringBuilder.append(BEGIN_VEVENT)
        stringBuilder.append("\n")

    }

    fun setSummary(summary: String): VEventBuilder {
        if(summary.isNotBlank()) {
            stringBuilder.append(SUMMARY)
            stringBuilder.append(summary)
            stringBuilder.append("\n")
        }
        return this
    }

    fun setDtStart(dtStart: String): VEventBuilder {
        if(dtStart.isNotBlank()) {
            stringBuilder.append(DTSTART)
            stringBuilder.append(dtStart)
            stringBuilder.append("\n")
        }
        return this
    }

    fun setDtEnd(dtEnd: String): VEventBuilder {
        if(dtEnd.isNotBlank()) {
            stringBuilder.append(DTEND)
            stringBuilder.append(dtEnd)
            stringBuilder.append("\n")
        }
        return this
    }

    fun setLocation(location: String): VEventBuilder {
        if(location.isNotBlank()) {
            stringBuilder.append(LOCATION)
            stringBuilder.append(location)
            stringBuilder.append("\n")
        }
        return this
    }

    fun setDescription(description: String): VEventBuilder {
        if(description.isNotBlank()) {
            stringBuilder.append(DESCRIPTION)
            stringBuilder.append(description)
            stringBuilder.append("\n")
        }
        return this
    }

    fun build(): String {
        stringBuilder.append(END_VEVENT)
        return stringBuilder.toString()
    }
}