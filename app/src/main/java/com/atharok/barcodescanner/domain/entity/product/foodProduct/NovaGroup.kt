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

enum class NovaGroup(val drawableResource: Int, val descriptionStringResource: Int) {
    GROUP_1(R.drawable.nova_group_1, R.string.nova_group_description_1),
    GROUP_2(R.drawable.nova_group_2, R.string.nova_group_description_2),
    GROUP_3(R.drawable.nova_group_3, R.string.nova_group_description_3),
    GROUP_4(R.drawable.nova_group_4, R.string.nova_group_description_4),
    UNKNOWN(-1, R.string.nova_group_description_unknown)
}