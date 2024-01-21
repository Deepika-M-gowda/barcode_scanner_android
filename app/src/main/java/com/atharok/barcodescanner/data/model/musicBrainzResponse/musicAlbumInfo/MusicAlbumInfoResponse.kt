package com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumInfo

import androidx.annotation.Keep
import com.atharok.barcodescanner.domain.entity.analysis.MusicBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPI
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.musicProduct.AlbumTrack
import com.atharok.barcodescanner.domain.entity.product.musicProduct.obtainAlbum
import com.atharok.barcodescanner.domain.entity.product.musicProduct.obtainArtists
import com.atharok.barcodescanner.domain.entity.product.musicProduct.obtainDate
import com.atharok.barcodescanner.domain.entity.product.musicProduct.obtainId
import com.atharok.barcodescanner.domain.entity.product.musicProduct.obtainTrackCount
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class MusicAlbumInfoResponse(
    @SerializedName("count")
    @Expose
    val count: Int? = null,

    @SerializedName("releases")
    @Expose
    val releases: List<ReleaseSchema>? = null
) {
    fun toModel(barcode: Barcode, source: RemoteAPI, coverUrl: String?, albumTracks: List<AlbumTrack>?): MusicBarcodeAnalysis = MusicBarcodeAnalysis(
        barcode = barcode,
        source = source,
        id = obtainId(this),
        artists = obtainArtists(this),
        album = obtainAlbum(this),
        date = obtainDate(this),
        trackCount = obtainTrackCount(this),
        coverUrl = coverUrl,
        albumTracks = albumTracks
    )
}