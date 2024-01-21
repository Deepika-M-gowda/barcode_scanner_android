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
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.canBeConvertibleToLong
import com.atharok.barcodescanner.common.utils.EAN_13_LENGTH
import com.atharok.barcodescanner.common.utils.EAN_8_LENGTH
import com.atharok.barcodescanner.common.utils.UPC_A_LENGTH
import com.atharok.barcodescanner.common.utils.UPC_E_LENGTH
import kotlin.math.ceil

class BarcodeFormatChecker(private val context: Context) {

    /**
     * @param contents The barcode contents to check.
     * @param length Barcode contents length.
     * @return Returns null if contents is correct. Otherwise, returns the error message.
     */
    private fun check(contents: String, length: Int, calculateCheckDigit: () -> Int): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            contents.length != length -> context.getString(R.string.error_barcode_wrong_length_message, length.toString())
            !contents.canBeConvertibleToLong() -> context.getString(R.string.error_barcode_not_a_number_message)
            else -> {
                val checkDigit: Int = calculateCheckDigit()
                if(checkDigit != Character.getNumericValue(contents.last())){
                    context.getString(R.string.error_barcode_wrong_key_message, length.toString(), checkDigit.toString())
                } else {
                    null
                }
            }
        }
    }

    fun checkEAN13Error(ean13: String): String? {
        return check(ean13, EAN_13_LENGTH) {
            calculateBarcodeCheckDigit(ean13, 1, 3, EAN_13_LENGTH)
        }
    }

    fun checkEAN8Error(ean8: String): String? {
        return check(ean8, EAN_8_LENGTH) {
            calculateBarcodeCheckDigit(ean8, 3, 1, EAN_8_LENGTH)
        }
    }

    fun checkUPCAError(upcA: String): String? {
        return check(upcA, UPC_A_LENGTH) {
            calculateBarcodeCheckDigit(upcA, 3, 1, UPC_A_LENGTH)
        }
    }

    fun checkUPCEError(upcE: String): String? {
        return when {
            upcE.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            upcE.length != UPC_E_LENGTH -> context.getString(R.string.error_barcode_wrong_length_message, UPC_E_LENGTH.toString())
            !upcE.canBeConvertibleToLong() -> context.getString(R.string.error_barcode_not_a_number_message)
            !upcE.startsWith("0") -> context.getString(R.string.error_barcode_upc_e_not_start_with_0_error_message)
            else -> {
                val checkDigit: Int = calculateBarcodeCheckDigit(upcE, 3, 1, UPC_E_LENGTH)
                if(checkDigit != Character.getNumericValue(upcE.last())){
                    context.getString(R.string.error_barcode_wrong_key_message, UPC_E_LENGTH.toString(), checkDigit.toString())
                } else {
                    null
                }
            }
        }
    }

    fun checkCode39Error(code39: String): String? {
        return when {
            code39.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkCode39Regex(code39) -> context.getString(R.string.error_barcode_39_regex_error_message)
            else -> null
        }
    }

    fun checkCode93Error(code93: String): String? {
        return when {
            code93.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkCode93Regex(code93) -> context.getString(R.string.error_barcode_93_regex_error_message)
            else -> null
        }
    }

    fun checkCode128Error(code128: String): String? {
        return when {
            code128.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkUSASCIIEncodingValue(code128) -> context.getString(R.string.error_barcode_encoding_us_ascii_error_message)
            else -> null
        }
    }

    fun checkITFError(itf: String): String? {
        return when {
            itf.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkITFBarcode(itf) -> context.getString(R.string.error_barcode_itf_error_message)
            else -> null
        }
    }

    fun checkCodabarError(codabar: String): String? {
        return when {
            codabar.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkCodabarRegex(codabar) -> context.getString(R.string.error_barcode_codabar_regex_error_message)
            else -> null
        }
    }

    fun checkDataMatrixError(dataMatrix: String): String? {
        return when {
            dataMatrix.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !checkISO88591EncodingValue(dataMatrix) -> context.getString(R.string.error_barcode_encoding_iso_8859_1_error_message)
            else -> null
        }
    }

    fun checkBlankError(contents: String): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            else -> null
        }
    }

    fun checkQrUrlError(contents: String): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_none_character_message)
            !contents.startsWith("http://") && !contents.startsWith("https://") -> {
                context.getString(R.string.error_barcode_qr_url_format_message)
            }
            else -> null
        }
    }

    fun checkQrPhoneNumberError(contents: String): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_qr_phone_number_missing_message)
            else -> null
        }
    }

    fun checkQrLocalisationError(contents: String): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_qr_localisation_missing_message)
            else -> null
        }
    }

    fun checkQrMailError(contents: String): String? {
        return when {
            contents.isBlank() -> context.getString(R.string.error_barcode_qr_email_missing_message)
            else -> null
        }
    }

    private fun calculateBarcodeCheckDigit(content: String, multPairValue: Int, multImpairValue: Int, maxLength: Int): Int {
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

    /**
     * Vérifie si la chaine de caractère en paramètre contient des caractères spéciaux.
     */
    private fun checkISO88591EncodingValue(content: String): Boolean {
        val byteArrayISO = content.toByteArray(Charsets.ISO_8859_1)
        val byteArrayUTF8 = content.toByteArray(Charsets.UTF_8)

        val strISO88591 = String(byteArrayISO, Charsets.ISO_8859_1)
        val strUTF8 = String(byteArrayUTF8, Charsets.UTF_8)

        return strISO88591 == strUTF8
    }

    /**
     * Vérifie si la chaine de caractère en paramètre contient des caractères spéciaux ou des accents.
     */
    private fun checkUSASCIIEncodingValue(content: String): Boolean {
        val byteArrayUSASCII = content.toByteArray(Charsets.US_ASCII)
        val byteArrayUTF8 = content.toByteArray(Charsets.UTF_8)

        val strUSASCII = String(byteArrayUSASCII, Charsets.ISO_8859_1)
        val strUTF8 = String(byteArrayUTF8, Charsets.UTF_8)

        return strUSASCII == strUTF8
    }

    private fun checkCode93Regex(content: String): Boolean {
        return content.matches("[A-Z0-9-. *$/+%]*".toRegex())
    }

    private fun checkCode39Regex(content: String): Boolean {
        return content.matches("[A-Z0-9-. $/+%]*".toRegex())
    }

    private fun checkCodabarRegex(content: String): Boolean {
        return content.matches(
            "^[A-Da-d][0-9-$:/.+]*[A-Da-d]$".toRegex()) || content.matches("[0-9-\$:/.+]*".toRegex()
        )
    }

    private fun checkITFBarcode(content: String): Boolean = content.length % 2 == 0
}