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

class BarcodeFormatCheckerResult(val response: CheckerResponse, val key: Int? = null, val length: Int? = null) {
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
}