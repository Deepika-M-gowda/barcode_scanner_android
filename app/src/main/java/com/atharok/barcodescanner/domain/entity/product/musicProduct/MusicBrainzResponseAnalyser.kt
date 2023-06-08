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

package com.atharok.barcodescanner.domain.entity.product.musicProduct

import com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumInfo.MusicAlbumInfoResponse
import com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumInfo.ReleaseSchema

fun obtainId(response: MusicAlbumInfoResponse): String? {
    val releases: List<ReleaseSchema>? = response.releases
    return if(!releases.isNullOrEmpty()) releases[0].id else null
}

fun obtainAlbum(response: MusicAlbumInfoResponse): String? {
    val releases: List<ReleaseSchema>? = response.releases
    return if(!releases.isNullOrEmpty()) releases[0].album else null
}

fun obtainArtists(response: MusicAlbumInfoResponse): List<String>? {
    val releases: List<ReleaseSchema>? = response.releases
    return if(!releases.isNullOrEmpty()) {
        releases[0].artists?.mapNotNull { it.name }
    } else null
}

fun obtainDate(response: MusicAlbumInfoResponse): String? {
    val releases: List<ReleaseSchema>? = response.releases
    return if(!releases.isNullOrEmpty()) releases[0].date else null
}

fun obtainTrackCount(response: MusicAlbumInfoResponse): Int? {
    val releases: List<ReleaseSchema>? = response.releases
    return if(!releases.isNullOrEmpty()) releases[0].trackCount else null
}