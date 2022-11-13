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

class Iban {

    companion object {
        private const val MIN_LENGTH = 14
        private const val MAX_LENGTH = 34
    }

    fun verify(text: String): Boolean {

        var success = false

        val step1 = text.filter { !it.isWhitespace() }.uppercase()

        if(step1.length in MIN_LENGTH..MAX_LENGTH){
            val step2: String = movePrefixToSuffix(step1)
            val step3: String? = convertLettersToDigitsCode(step2)

            if(step3 != null) {
                val step4: Int? = calculateModulo97(step3)
                if(step4 != null)
                    success = step4 == 1
            }
        }

        return success
    }

    private fun movePrefixToSuffix(text: String): String {
        val prefix = text.subSequence(0, 4)
        return text.removePrefix(prefix)+prefix
    }

    private fun convertLettersToDigitsCode(text: String): String? {
        var error = false
        val stringBuilder: StringBuilder = StringBuilder()
        for(char in text){
            when{
                char.isLetter() -> stringBuilder.append(convertCharToDigit(char))
                char.isDigit() -> stringBuilder.append(char)
                else -> {error = true; break}
            }
        }
        return if(error) null else stringBuilder.toString()
    }

    private fun calculateModulo97(text: String): Int? {
        var error = false
        val stringBuilder = StringBuilder()
        for(i in text.indices step 1){
            stringBuilder.append(text[i])

            if(stringBuilder.length>=8 || i == text.length-1) {
                try {
                    val value = stringBuilder.toString().toInt()
                    stringBuilder.clear()
                    stringBuilder.append(value%97)
                } catch (e: NumberFormatException) {
                    error = true
                    break
                }
            }
        }

        return if(error) null else stringBuilder.toString().toInt()
    }

    /**
     * On part du code ASCII (A=65, B=66, C=67, ...) et on lui retire 55 pour obtenir le chiffre
     * voulu pour le calcul de l'IBAN (A=10, B=11, C=12, ...).
     */
    private fun convertCharToDigit(char: Char): Int = char.code-55
}