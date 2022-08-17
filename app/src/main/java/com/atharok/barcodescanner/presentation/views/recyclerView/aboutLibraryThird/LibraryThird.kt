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

package com.atharok.barcodescanner.presentation.views.recyclerView.aboutLibraryThird

import com.atharok.barcodescanner.R

enum class LibraryThird(val nameResource: Int, val licenseResource: Int, val authorResource: Int, val descriptionResource: Int, val webLinkResource: Int) {
    ZXING(R.string.dependency_zxing_title_label, R.string.dependency_zxing_license_label, R.string.dependency_zxing_author_label, R.string.dependency_zxing_description_label, R.string.dependency_zxing_github_link_label),
    ZXING_CODE_SCANNER(R.string.dependency_zxing_code_scanner_title_label, R.string.dependency_zxing_code_scanner_license_label, R.string.dependency_zxing_code_scanner_author_label, R.string.dependency_zxing_code_scanner_description_label, R.string.dependency_zxing_code_scanner_github_link_label),
    ANDROID_IMAGE_CROPPER(R.string.dependency_android_image_cropper_title_label, R.string.dependency_android_image_cropper_license_label, R.string.dependency_android_image_cropper_author_label, R.string.dependency_android_image_cropper_description_label, R.string.dependency_android_image_cropper_github_link_label),
    EZ_VCARD(R.string.dependency_ez_vcard_title_label, R.string.dependency_ez_vcard_license_label, R.string.dependency_ez_vcard_author_label, R.string.dependency_ez_vcard_description_label, R.string.dependency_ez_vcard_github_link_label),
    GLIDE(R.string.dependency_glide_title_label, R.string.dependency_glide_license_label, R.string.dependency_glide_author_label, R.string.dependency_glide_description_label, R.string.dependency_glide_github_link_label),
    GLIDE_TO_VECTOR_YOU(R.string.dependency_glide_to_vector_you_title_label, R.string.dependency_glide_to_vector_you_license_label, R.string.dependency_glide_to_vector_you_author_label, R.string.dependency_glide_to_vector_you_description_label, R.string.dependency_glide_to_vector_you_github_link_label),
    MATERIAL_COMPONENTS(R.string.dependency_material_components_title_label, R.string.dependency_material_components_license_label, R.string.dependency_material_components_author_label, R.string.dependency_material_components_description_label, R.string.dependency_material_components_github_link_label),
    //KOTLIN_X_COROUTINE_ANDROID(R.string.dependency_kotlinx_coroutines_title_label, R.string.dependency_kotlinx_coroutines_description_label, R.string.dependency_kotlinx_coroutines_github_link_label),
    KOIN(R.string.dependency_koin_title_label, R.string.dependency_koin_license_label, R.string.dependency_koin_author_label, R.string.dependency_koin_description_label, R.string.dependency_koin_github_link_label),
    GSON(R.string.dependency_gson_title_label, R.string.dependency_gson_license_label, R.string.dependency_gson_author_label, R.string.dependency_gson_description_label, R.string.dependency_gson_github_link_label),
    RETROFIT(R.string.dependency_retrofit_title_label, R.string.dependency_retrofit_license_label, R.string.dependency_retrofit_author_label, R.string.dependency_retrofit_description_label, R.string.dependency_retrofit_github_link_label),
    ROOM(R.string.dependency_room_title_label, R.string.dependency_room_license_label, R.string.dependency_room_author_label, R.string.dependency_room_description_label, R.string.dependency_room_remote_link_label),
    //LIVE_DATA(R.string.dependency_live_data_title_label, R.string.dependency_live_data_description_label, R.string.dependency_live_data_github_link_label)
}