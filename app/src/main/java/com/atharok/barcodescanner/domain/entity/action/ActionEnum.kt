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

package com.atharok.barcodescanner.domain.entity.action

import com.atharok.barcodescanner.R
import java.io.Serializable

enum class ActionEnum(val stringResource: Int, val drawableResource: Int): Serializable {
    ADD_AGENDA(R.string.qr_code_type_name_agenda, R.drawable.baseline_event_note_24),

    ADD_CONTACT(R.string.action_add_contact_label, R.drawable.baseline_contacts_24),

    ADD_PHONE_NUMBER(R.string.action_add_contact_label, R.drawable.baseline_contacts_24),
    ADD_SMS_NUMBER(R.string.action_add_contact_label, R.drawable.baseline_contacts_24),
    ADD_MAIL(R.string.action_add_contact_label, R.drawable.baseline_contacts_24),

    CALL_PHONE_NUMBER(R.string.action_call_phone_label, R.drawable.baseline_call_24),
    CALL_SMS_NUMBER(R.string.action_call_phone_label, R.drawable.baseline_call_24),

    SEND_SMS_TO_PHONE_NUMBER(R.string.action_send_sms_label, R.drawable.baseline_textsms_24),
    SEND_SMS(R.string.action_send_sms_label, R.drawable.baseline_textsms_24),
    SEND_MAIL(R.string.action_send_mail_label, R.drawable.baseline_mail_24),

    SEARCH_LOCALISATION(R.string.qr_code_type_name_geographic_coordinates, R.drawable.baseline_place_24),

    OPEN_PHONE_DIALOG(R.string.action_title_dialog_label, R.drawable.baseline_call_24),
    OPEN_MAIL_DIALOG(R.string.action_title_dialog_label, R.drawable.baseline_mail_24),
    OPEN_SEARCH_DIALOG(R.string.search_label, R.drawable.baseline_search_24),

    OPEN_IN_WEB_BROWSER(R.string.action_go_to_url_label, R.drawable.outline_open_in_browser_24),
    SEARCH_WITH_ENGINE(R.string.action_web_search_label, R.drawable.baseline_search_24),

    SHARE_TEXT(R.string.share_label, R.drawable.baseline_share_24),
    SHARE_IMAGE(R.string.share_image_label, R.drawable.baseline_share_24),
    COPY_TEXT(R.string.copy_label, R.drawable.baseline_content_copy_24),
    CONFIGURE_WIFI(R.string.qr_code_type_name_wifi, R.drawable.baseline_wifi_24)
}