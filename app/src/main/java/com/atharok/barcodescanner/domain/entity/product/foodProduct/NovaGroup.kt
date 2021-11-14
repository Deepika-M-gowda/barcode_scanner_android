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

enum class NovaGroup(val novaGroupImageUrl: String?, val descriptionStringResource: Int) {
    GROUP_1(NOVA_GROUP_1_URL, R.string.nova_group_description_1),
    GROUP_2(NOVA_GROUP_2_URL, R.string.nova_group_description_2),
    GROUP_3(NOVA_GROUP_3_URL, R.string.nova_group_description_3),
    GROUP_4(NOVA_GROUP_4_URL, R.string.nova_group_description_4),
    UNKNOWN(NOVA_GROUP_UNKNOWN_URL, R.string.nova_group_description_unknown)
}