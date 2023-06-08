package com.atharok.barcodescanner.data.model.covertArtArchiveResponse

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Thumbnails(
    @SerializedName("large")
    @Expose
    val large: String? = null,

    @SerializedName("small")
    @Expose
    val small: String? = null
)