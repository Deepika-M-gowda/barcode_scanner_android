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

import android.os.Build
import android.text.Html
import android.text.Spanned
import java.util.Locale

/**
 * Cette méthode permet d'enjoliver une chaine de caractère. Elle est utilisée pour les textes
 * d'Open Food Facts principalement.
 *
 * Sépare la chaine au niveau des virgules, et réassemble le tout avec:
 *      - La méthode trim(), permettant de suprimer les espaces inutile en début et fin de chaine
 *      - La méthode capitalize() mettant en majuscule chaque premier mot de la chaine.
 */
fun String.polishText(): String {
    return if(this.isNotBlank()){
        val textSplited = this.split(',')
        val strBuilder = StringBuilder()

        textSplited.forEach { text ->

            strBuilder.append(text.trim().firstCharacterIntoCapital())

            if(text!=textSplited.last())
                strBuilder.append(", ")
        }

        strBuilder.toString()
    } else this
}

fun String.canBeConvertibleToLong(): Boolean = this.toLongOrNull()?.let { true } ?: false

fun String.toHtmlSpanned(): Spanned =
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }

fun String.firstCharacterIntoCapital(): String = replaceFirstChar {
    if (it.isLowerCase())
        it.titlecase(Locale.getDefault())
    else
        it.toString()
}