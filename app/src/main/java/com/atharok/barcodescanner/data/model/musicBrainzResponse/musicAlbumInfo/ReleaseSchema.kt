package com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumInfo

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class ReleaseSchema(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("title")
    @Expose
    val album: String? = null,

    @SerializedName("artist-credit")
    @Expose
    val artists: List<ArtistSchema>? = null,

    @SerializedName("date")
    @Expose
    val date: String? = null,

    @SerializedName("track-count")
    @Expose
    val trackCount: Int? = null,
)