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

package com.atharok.barcodescanner.domain.entity.barcode

import com.atharok.barcodescanner.R
import java.io.Serializable

enum class BarcodeType(val stringResource: Int, val drawableResource: Int): Serializable {
    AGENDA(R.string.qr_code_type_name_agenda, R.drawable.baseline_event_note_24),
    CONTACT(R.string.qr_code_type_name_contact, R.drawable.baseline_contacts_24),
    LOCALISATION(R.string.qr_code_type_name_geographic_coordinates, R.drawable.baseline_place_24),
    MAIL(R.string.qr_code_type_name_mail, R.drawable.baseline_mail_24),
    PHONE(R.string.qr_code_type_name_phone, R.drawable.baseline_call_24),
    SMS(R.string.qr_code_type_name_sms, R.drawable.baseline_textsms_24),
    TEXT(R.string.qr_code_type_name_text, R.drawable.baseline_text_fields_24),
    URL(R.string.qr_code_type_name_web_site, R.drawable.baseline_web_24),
    WIFI(R.string.qr_code_type_name_wifi, R.drawable.baseline_wifi_24),
    FOOD(R.string.bar_code_type_food, R.drawable.baseline_restaurant_24),
    PET_FOOD(R.string.bar_code_type_pet_food, R.drawable.baseline_pets_24),
    BEAUTY(R.string.bar_code_type_beauty, R.drawable.baseline_face_24),
    BOOK(R.string.bar_code_type_book, R.drawable.ic_book_24),
    INDUSTRIAL(R.string.bar_code_type_industrial, R.drawable.ic_bar_code_24),
    MATRIX(R.string.bar_code_type_unknown_matrix, R.drawable.baseline_qr_code_24),
    UNKNOWN(R.string.bar_code_type_name_unknown, R.drawable.ic_bar_code_24),
    UNKNOWN_PRODUCT(R.string.bar_code_type_unknown_product, R.drawable.ic_bar_code_24)
}