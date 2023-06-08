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

package com.atharok.barcodescanner.data.repositories

import com.atharok.barcodescanner.data.api.CoverArtArchiveService
import com.atharok.barcodescanner.data.api.MusicBrainzService
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.RemoteAPI
import com.atharok.barcodescanner.domain.entity.product.musicProduct.AlbumTrack
import com.atharok.barcodescanner.domain.entity.product.musicProduct.MusicBarcodeAnalysis
import com.atharok.barcodescanner.domain.repositories.MusicProductRepository

class MusicProductRepositoryImpl(private val musicBrainzService: MusicBrainzService,
                                 private val coverArtService: CoverArtArchiveService): MusicProductRepository {

    override suspend fun getMusicProduct(barcode: Barcode): MusicBarcodeAnalysis? {
        val musicAlbumInfoResponse = musicBrainzService.getAlbumInfoFromBarcode("barcode:${barcode.contents}")

        if(musicAlbumInfoResponse?.releases == null || musicAlbumInfoResponse.count == 0)
            return null

        val id: String? = musicAlbumInfoResponse.releases.firstOrNull()?.id
        val coverUrl: String? = id?.let { getCoverArtUrl(it) }
        val albumTracks: List<AlbumTrack>? = id?.let { getAlbumTracks(it) }

        return musicAlbumInfoResponse.toModel(barcode, RemoteAPI.MUSICBRAINZ, coverUrl, albumTracks)
    }

    private suspend fun getCoverArtUrl(discId: String): String? {
        return try {
            val response = coverArtService.getCoverArt(discId)
            (response?.images?.firstOrNull()?.thumbnails?.small ?: response?.images?.firstOrNull()?.imageUrl)?.replace("http://", "https://")
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getAlbumTracks(discId: String): List<AlbumTrack>? {
        return try {
            val response = musicBrainzService.getAlbumTracks(discId)
            response?.toModel()
        } catch (e: Exception) {
            null
        }
    }
}