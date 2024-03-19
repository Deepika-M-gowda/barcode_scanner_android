package com.atharok.barcodescanner.domain.entity.customUrl

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.atharok.barcodescanner.common.utils.CUSTOM_SEARCH_URL_BARCODE_KEY_WORD
import java.io.Serializable

@Keep
@Entity
data class CustomUrl(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val url: String,
): Serializable {
    @Ignore
    fun getUrlWithContents(contents: String): String = url.replace(CUSTOM_SEARCH_URL_BARCODE_KEY_WORD, contents)

}