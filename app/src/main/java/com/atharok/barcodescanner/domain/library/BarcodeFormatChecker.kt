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

import kotlin.math.ceil

class BarcodeFormatChecker {

    enum class CheckerResponse {
        BAR_CODE_SUCCESSFUL,
        BAR_CODE_NOT_A_NUMBER_ERROR,
        BAR_CODE_WRONG_LENGTH_ERROR,
        BAR_CODE_WRONG_KEY_ERROR,
        BAR_CODE_ENCODING_ISO_8859_1_ERROR,
        BAR_CODE_ENCODING_US_ASCII_ERROR,
        BAR_CODE_CODE_93_REGEX_ERROR,
        BAR_CODE_CODE_39_REGEX_ERROR,
        BAR_CODE_CODABAR_REGEX_ERROR,
        BAR_CODE_ITF_ERROR,
        BAR_CODE_UPC_E_NOT_START_WITH_0_ERROR,
    }

    /*fun check(content: String, format: BarcodeFormat): CheckerResponse = when(format){
        BarcodeFormat.EAN_13 -> checkBarCode(content, 1, 3, 13)
        BarcodeFormat.EAN_8 -> checkBarCode(content, 3, 1, 8)
        BarcodeFormat.UPC_A -> checkBarCode(content, 3, 1, 12)
        BarcodeFormat.UPC_E -> checkUPCE(content)
        BarcodeFormat.DATA_MATRIX -> checkISO88591EncodingValue(content)
        BarcodeFormat.CODE_128 -> checkUSASCIIEncodingValue(content)
        BarcodeFormat.CODE_93 -> checkCode93Regex(content)
        BarcodeFormat.CODE_39 -> checkCode39Regex(content)
        BarcodeFormat.CODABAR -> checkCodabarRegex(content)
        BarcodeFormat.ITF -> checkITFBarCode(content)
        else -> CheckerResponse.BAR_CODE_SUCCESSFUL
    }*/

    fun calculateEAN13CheckDigit(content: String): Int {
        return calculateBarcodeCheckDigit(content, 1, 3, 13)
    }

    fun calculateEAN8CheckDigit(content: String): Int {
        return calculateBarcodeCheckDigit(content, 3, 1, 8)
    }

    fun calculateUPCACheckDigit(content: String): Int {
        return calculateBarcodeCheckDigit(content, 3, 1, 12)
    }

    fun calculateUPCECheckDigit(content: String): Int {
        return calculateBarcodeCheckDigit(content, 3, 1, 8)
    }

    private fun calculateBarcodeCheckDigit(content: String, multPairValue: Int, multImpairValue: Int, maxLength: Int): Int {
        //if (content.length != maxLength) return CheckerResponse.BAR_CODE_WRONG_LENGTH_ERROR
        //if(!content.canBeConvertibleToLong()) return CheckerResponse.BAR_CODE_NOT_A_NUMBER_ERROR

        var sum = 0
        for ((i, char) in content.withIndex()) {
            if (i >= maxLength-1) break

            val p = if (i % 2 == 0) multPairValue else multImpairValue
            val num = Character.getNumericValue(char)

            sum += (num * p)
        }
        val remainder = sum % 10

        val max: Int = ceil(remainder/10f).toInt() * 10

        val key = max - remainder

        return key
    }

    /*private fun checkUPCE(content: String): CheckerResponse {
        val result = checkBarCode(content, 3, 1, 8)

        return if (result == CheckerResponse.BAR_CODE_SUCCESSFUL && !content.startsWith("0"))
            CheckerResponse.BAR_CODE_UPC_E_NOT_START_WITH_0_ERROR
        else result
    }*/

    /**
     * Vérifie si la chaine de caractère en paramètre contient des caractères spéciaux.
     */
    fun checkISO88591EncodingValue(content: String): Boolean {

        val byteArrayISO = content.toByteArray(Charsets.ISO_8859_1)
        val byteArrayUTF8 = content.toByteArray(Charsets.UTF_8)

        val strISO88591 = String(byteArrayISO, Charsets.ISO_8859_1)
        val strUTF8 = String(byteArrayUTF8, Charsets.UTF_8)

        return strISO88591 == strUTF8
    }

    /**
     * Vérifie si la chaine de caractère en paramètre contient des caractères spéciaux ou des accents.
     */
    fun checkUSASCIIEncodingValue(content: String): Boolean {
        val byteArrayUSASCII = content.toByteArray(Charsets.US_ASCII)
        val byteArrayUTF8 = content.toByteArray(Charsets.UTF_8)

        val strUSASCII = String(byteArrayUSASCII, Charsets.ISO_8859_1)
        val strUTF8 = String(byteArrayUTF8, Charsets.UTF_8)

        return strUSASCII == strUTF8
    }

    fun checkCode93Regex(content: String): Boolean {
        return content.matches("[A-Z0-9-. *$/+%]*".toRegex())
    }

    fun checkCode39Regex(content: String): Boolean {
        return content.matches("[A-Z0-9-. $/+%]*".toRegex())
    }

    fun checkCodabarRegex(content: String): Boolean {
        return content.matches(
            "^[A-Da-d][0-9-$:/.+]*[A-Da-d]$".toRegex()) || content.matches("[0-9-\$:/.+]*".toRegex()
        )
    }

    fun checkITFBarcode(content: String): Boolean = content.length % 2 == 0
}