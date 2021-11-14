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

package com.atharok.barcodescanner.domain.entity.product.foodProduct

import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.*

enum class Nutriscore(val nutriscoreImageUrl: String, val descriptionStringResource: Int) {
    A(NUTRISCORE_A_URL, R.string.nutriscore_description_a),
    B(NUTRISCORE_B_URL, R.string.nutriscore_description_b),
    C(NUTRISCORE_C_URL, R.string.nutriscore_description_c),
    D(NUTRISCORE_D_URL, R.string.nutriscore_description_d),
    E(NUTRISCORE_E_URL, R.string.nutriscore_description_e),
    UNKNOWN(NUTRISCORE_UNKNOWN_URL, R.string.nutriscore_description_unknown)
}