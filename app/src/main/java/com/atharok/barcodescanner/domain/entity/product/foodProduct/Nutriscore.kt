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

enum class Nutriscore(val drawableResource: Int, val descriptionStringResource: Int) {
    A(R.drawable.nutriscore_a, R.string.nutriscore_description_a),
    B(R.drawable.nutriscore_b, R.string.nutriscore_description_b),
    C(R.drawable.nutriscore_c, R.string.nutriscore_description_c),
    D(R.drawable.nutriscore_d, R.string.nutriscore_description_d),
    E(R.drawable.nutriscore_e, R.string.nutriscore_description_e),
    UNKNOWN(-1, R.string.nutriscore_description_unknown)
}