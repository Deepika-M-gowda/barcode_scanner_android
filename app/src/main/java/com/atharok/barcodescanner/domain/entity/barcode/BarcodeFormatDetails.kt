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
import com.google.zxing.BarcodeFormat
import java.io.Serializable

enum class BarcodeFormatDetails(val stringResource: Int, val drawableResource: Int, val format: BarcodeFormat): Serializable {

    QR_TEXT(R.string.qr_code_type_name_text, R.drawable.baseline_text_fields_24, BarcodeFormat.QR_CODE),
    QR_AGENDA(R.string.qr_code_type_name_agenda, R.drawable.baseline_event_note_24, BarcodeFormat.QR_CODE),
    QR_CONTACT(R.string.qr_code_type_name_contact, R.drawable.baseline_contacts_24, BarcodeFormat.QR_CODE),
    QR_EPC(R.string.qr_code_type_name_epc, R.drawable.baseline_qr_code_24, BarcodeFormat.QR_CODE),
    QR_LOCALISATION(R.string.qr_code_type_name_geographic_coordinates, R.drawable.baseline_place_24, BarcodeFormat.QR_CODE),
    QR_MAIL(R.string.qr_code_type_name_mail, R.drawable.baseline_mail_24, BarcodeFormat.QR_CODE),
    QR_PHONE(R.string.qr_code_type_name_phone, R.drawable.baseline_call_24, BarcodeFormat.QR_CODE),
    QR_SMS(R.string.qr_code_type_name_sms, R.drawable.baseline_textsms_24, BarcodeFormat.QR_CODE),
    QR_URL(R.string.qr_code_type_name_web_site, R.drawable.baseline_web_24, BarcodeFormat.QR_CODE),
    QR_WIFI(R.string.qr_code_type_name_wifi, R.drawable.baseline_wifi_24, BarcodeFormat.QR_CODE),
    DATA_MATRIX(R.string.bar_code_data_matrix_label, R.drawable.ic_data_matrix_code_24, BarcodeFormat.DATA_MATRIX),
    PDF_417(R.string.bar_code_pdf_417_label, R.drawable.ic_pdf_417_code_24, BarcodeFormat.PDF_417),
    AZTEC(R.string.bar_code_aztec_label, R.drawable.ic_aztec_code_24, BarcodeFormat.AZTEC),
    EAN_13(R.string.bar_code_ean_13_label, R.drawable.ic_bar_code_24, BarcodeFormat.EAN_13),
    EAN_8(R.string.bar_code_ean_8_label, R.drawable.ic_bar_code_24, BarcodeFormat.EAN_8),
    UPC_A(R.string.bar_code_upc_a_label, R.drawable.ic_bar_code_24, BarcodeFormat.UPC_A),
    UPC_E(R.string.bar_code_upc_e_label, R.drawable.ic_bar_code_24, BarcodeFormat.UPC_E),
    CODE_128(R.string.bar_code_code_128_label, R.drawable.ic_bar_code_24, BarcodeFormat.CODE_128),
    CODE_93(R.string.bar_code_code_93_label, R.drawable.ic_bar_code_24, BarcodeFormat.CODE_93),
    CODE_39(R.string.bar_code_code_39_label, R.drawable.ic_bar_code_24, BarcodeFormat.CODE_39),
    CODABAR(R.string.bar_code_codabar_label, R.drawable.ic_bar_code_24, BarcodeFormat.CODABAR),
    ITF(R.string.bar_code_itf_label, R.drawable.ic_bar_code_24, BarcodeFormat.ITF)
}