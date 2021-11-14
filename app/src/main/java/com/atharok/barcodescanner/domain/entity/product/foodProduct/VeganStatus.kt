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

import androidx.annotation.AttrRes
import com.atharok.barcodescanner.R

enum class VeganStatus(val stringResource: Int, @AttrRes val colorResource: Int, val drawableResource: Int) {
    VEGAN(R.string.is_vegan_label, R.attr.colorPositive, R.drawable.baseline_check_circle_outline_24),
    NO_VEGAN(R.string.no_vegan_label, R.attr.colorNegative, R.drawable.baseline_highlight_off_24),
    MAYBE_VEGAN(R.string.maybe_vegan_label, R.attr.colorMedium, R.drawable.baseline_help_outline_24),
    UNKNOWN_VEGAN(R.string.vegan_status_unknown_label, R.attr.colorUnknown, R.drawable.baseline_help_outline_24)
}