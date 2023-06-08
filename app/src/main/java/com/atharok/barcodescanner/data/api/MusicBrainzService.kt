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

package com.atharok.barcodescanner.data.api

import com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumInfo.MusicAlbumInfoResponse
import com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumTracks.MusicAlbumTracksResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicBrainzService {
    @GET("release/")
    suspend fun getAlbumInfoFromBarcode(@Query("query") barcode: String, @Query("fmt") fmt: String = "json"): MusicAlbumInfoResponse?

    @GET("release/{DISC_ID}")
    suspend fun getAlbumTracks(@Path("DISC_ID") discId: String, @Query("inc") inc: String = "recordings", @Query("fmt") fmt: String = "json"): MusicAlbumTracksResponse?
}