package com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumTracks

import androidx.annotation.Keep
import com.atharok.barcodescanner.domain.entity.product.musicProduct.AlbumTrack
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class MusicAlbumTracksResponse(
    @SerializedName("count")
    @Expose
    val count: Int? = null,

    @SerializedName("media")
    @Expose
    val medias: List<MediaSchema>? = null
) {
    fun toModel(): List<AlbumTrack>? {
        return medias?.firstOrNull()?.tracks?.map {
            AlbumTrack(it.title, it.length, it.position)
        }
    }
}