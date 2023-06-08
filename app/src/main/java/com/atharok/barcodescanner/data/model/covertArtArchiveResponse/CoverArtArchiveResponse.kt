package com.atharok.barcodescanner.data.model.covertArtArchiveResponse

import androidx.annotation.Keep
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class CoverArtArchiveResponse(
    @SerializedName("images")
    @Expose
    val images: List<ImageSchema>? = null
)