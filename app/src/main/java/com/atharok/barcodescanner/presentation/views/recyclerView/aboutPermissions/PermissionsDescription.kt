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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutPermissions

import com.atharok.barcodescanner.R

enum class PermissionsDescription(val nameResource: Int, val descriptionResource: Int) {
    CAMERA(R.string.permission_camera_label, R.string.permission_camera_description_label),
    INTERNET(R.string.permission_internet_label, R.string.permission_internet_description_label),
    CONTACT(R.string.permission_contact_label, R.string.permission_contact_description_label),
    LOCATION(R.string.permission_location_label, R.string.permission_location_description_label),
    WIFI(R.string.permission_wifi_label, R.string.permission_wifi_description_label),
    VIBRATE(R.string.permission_vibrate_label, R.string.permission_vibrate_description_label),
    //QUERY_ALL_PACKAGES(R.string.permission_query_all_packages_label, R.string.permission_query_all_packages_description_label)
}