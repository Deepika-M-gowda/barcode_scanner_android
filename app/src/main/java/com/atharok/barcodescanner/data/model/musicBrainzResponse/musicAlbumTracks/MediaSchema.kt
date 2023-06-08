package com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumTracks

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class MediaSchema(
    @SerializedName("tracks")
    @Expose
    val tracks: List<AlbumTrackSchema>? = null
)