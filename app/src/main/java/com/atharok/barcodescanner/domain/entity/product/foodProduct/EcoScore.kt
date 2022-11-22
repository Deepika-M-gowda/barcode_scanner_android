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

enum class EcoScore(val drawableResource: Int, val descriptionStringResource: Int) {
    A(R.drawable.ecoscore_a, R.string.eco_score_description_a),
    B(R.drawable.ecoscore_b, R.string.eco_score_description_b),
    C(R.drawable.ecoscore_c, R.string.eco_score_description_c),
    D(R.drawable.ecoscore_d, R.string.eco_score_description_d),
    E(R.drawable.ecoscore_e, R.string.eco_score_description_e),
    UNKNOWN(-1, R.string.eco_score_description_unknown)
}