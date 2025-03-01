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

package com.atharok.barcodescanner.data.model.openLibraryResponse

import androidx.annotation.Keep
import com.atharok.barcodescanner.domain.entity.analysis.BookBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPI
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainAuthorsStringList
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainCategories
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainContributions
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainCoverUrl
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainDescription
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainNumberPages
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainOriginalTitle
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainPublishDate
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainPublishers
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainSubtitle
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainTitle
import com.atharok.barcodescanner.domain.entity.product.bookProduct.obtainUrl
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class OpenLibraryResponse(
    @SerializedName("information")
    @Expose
    val informationSchema: InformationSchema? = null
) {

    fun toModel(barcode: Barcode, source: RemoteAPI): BookBarcodeAnalysis = BookBarcodeAnalysis(
        barcode =  barcode,
        source = source,
        url = obtainUrl(this),
        title = obtainTitle(this),
        subtitle = obtainSubtitle(this),
        originalTitle = obtainOriginalTitle(this),
        authors = obtainAuthorsStringList(this),
        coverUrl = obtainCoverUrl(this),
        description = obtainDescription(this),
        publishDate = obtainPublishDate(this),
        numberPages = obtainNumberPages(this),
        contributions = obtainContributions(this),
        publishers = obtainPublishers(this),
        categories = obtainCategories(this)
    )
}