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

enum class EcoScore(val ecoScoreImageUrl: String, val descriptionStringResource: Int) {
    A(ECO_SCORE_A_URL, R.string.eco_score_description_a),
    B(ECO_SCORE_B_URL, R.string.eco_score_description_b),
    C(ECO_SCORE_C_URL, R.string.eco_score_description_c),
    D(ECO_SCORE_D_URL, R.string.eco_score_description_d),
    E(ECO_SCORE_E_URL, R.string.eco_score_description_e),
    UNKNOWN(ECO_SCORE_UNKNOWN_URL, R.string.eco_score_description_unknown)
}