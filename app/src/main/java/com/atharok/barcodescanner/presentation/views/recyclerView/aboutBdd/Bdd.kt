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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutBdd

import com.atharok.barcodescanner.R

enum class Bdd(val nameResource: Int, val descriptionResource: Int, val webLinkResource: Int, val drawableResource: Int) {
    OPEN_FOOD_FACTS(R.string.open_food_facts_label, R.string.bdd_open_food_facts_description_label, R.string.bdd_open_food_facts_link_label, R.drawable.open_food_facts_logo_en_356x300),
    OPEN_PET_FOOD_FACTS(R.string.open_pet_food_facts_label, R.string.bdd_open_pet_food_facts_description_label, R.string.bdd_open_pet_food_facts_link_label, R.drawable.open_pet_food_facts_logo_en_356x300),
    OPEN_BEAUTY_FACTS(R.string.open_beauty_facts_label, R.string.bdd_open_beauty_facts_description_label, R.string.bdd_open_beauty_facts_link_label, R.drawable.open_beauty_facts_logo_en_356x300),
    OPEN_LIBRARY_FACTS(R.string.open_library_label, R.string.bdd_open_library_description_label, R.string.bdd_open_library_link_label, R.drawable.open_library_logo_356x87)
}