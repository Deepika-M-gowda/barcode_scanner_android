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

package com.atharok.barcodescanner.domain.entity.barcode

import com.atharok.barcodescanner.R
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.io.Serializable

enum class QrCodeErrorCorrectionLevel(val stringResource: Int, val errorCorrectionLevel: ErrorCorrectionLevel?): Serializable {
    L(R.string.qr_code_error_correction_level_name_low, ErrorCorrectionLevel.L),
    M(R.string.qr_code_error_correction_level_name_medium, ErrorCorrectionLevel.M),
    Q(R.string.qr_code_error_correction_level_name_quartile, ErrorCorrectionLevel.Q),
    H(R.string.qr_code_error_correction_level_name_high, ErrorCorrectionLevel.H),
    NONE(R.string.empty, null)
}