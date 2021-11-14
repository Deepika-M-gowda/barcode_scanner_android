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

package com.atharok.barcodescanner.domain.entity.product

import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType

enum class ApiSource(val nameResource: Int, val urlResource: Int, val barcodeType: BarcodeType, val layout: Int) {
    OPEN_LIBRARY(R.string.open_library_label, R.string.search_engine_open_library_url, BarcodeType.BOOK, R.layout.template_quote_open_library_layout),
    OPEN_FOOD_FACTS(R.string.open_food_facts_label, R.string.search_engine_open_food_facts_url, BarcodeType.FOOD, R.layout.template_quote_open_food_facts_layout),
    OPEN_PET_FOOD_FACTS(R.string.open_pet_food_facts_label, R.string.search_engine_open_pet_food_facts_url, BarcodeType.PET_FOOD, R.layout.template_quote_open_pet_food_facts_layout),
    OPEN_BEAUTY_FACTS(R.string.open_beauty_facts_label, R.string.search_engine_open_beauty_facts_url, BarcodeType.BEAUTY, R.layout.template_quote_open_beauty_facts_layout),
    NONE(-1, -1, BarcodeType.UNKNOWN_PRODUCT, -1)
}