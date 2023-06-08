package com.atharok.barcodescanner.data.model.musicBrainzResponse.musicAlbumTracks

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class AlbumTrackSchema(
    @SerializedName("id")
    @Expose
    val id: String? = null,

    @SerializedName("title")
    @Expose
    val title: String? = null,

    @SerializedName("length")
    @Expose
    val length: Long? = null,

    @SerializedName("position")
    @Expose
    val position: Int? = null,

    @SerializedName("number")
    @Expose
    val number: String? = null
)