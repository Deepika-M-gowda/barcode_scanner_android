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

enum class NutritionFactsEnum(val stringResource: Int) {
    ENERGY_KJ(R.string.off_energy_kj_label),
    ENERGY_KCAL(R.string.off_energy_kcal_label),
    FAT(R.string.off_fat_label),
    SATURATED_FAT(R.string.off_saturated_fat_label),
    CARBOHYDRATES(R.string.off_carbohydrates_label),
    SUGARS(R.string.off_sugars_label),
    STARCH(R.string.off_starch_label),
    FIBER(R.string.off_fiber_label),
    PROTEINS(R.string.off_proteins_label),
    SALT(R.string.off_salt_label),
    SODIUM(R.string.off_sodium_label)
}