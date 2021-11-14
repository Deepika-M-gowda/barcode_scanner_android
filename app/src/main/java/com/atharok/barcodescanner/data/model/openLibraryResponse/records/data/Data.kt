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

package com.atharok.barcodescanner.data.model.openLibraryResponse.records.data

import androidx.annotation.Keep
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.commons.Author
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.commons.CoverSchema
import com.atharok.barcodescanner.data.model.openLibraryResponse.records.commons.Identifiers
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

@Keep
data class Data(
    @SerializedName("publishers")
    @Expose
    val publishers: List<Name>? = null,

    @SerializedName("number_of_pages")
    @Expose
    val numberOfPages: Int? = null,
    
    @SerializedName("subtitle")
    @Expose
    val subtitle: String? = null,
    
    @SerializedName("links")
    @Expose
    val links: List<Link>? = null,
    
    @SerializedName("weight")
    @Expose
    val weight: String? = null,
    
    @SerializedName("title")
    @Expose
    val title: String? = null,
    
    @SerializedName("url")
    @Expose
    val url: String? = null,
    
    @SerializedName("identifiers")
    @Expose
    val identifiers: Identifiers? = null,
    
    @SerializedName("cover")
    @Expose
    val cover: CoverSchema? = null,
    
    @SerializedName("subject_places")
    @Expose
    val subjectPlaces: List<UrlNameSchema>? = null,
    
    @SerializedName("subjects")
    @Expose
    val subjects: List<UrlNameSchema>? = null,
    
    @SerializedName("subject_people")
    @Expose
    val subjectPeople: List<UrlNameSchema>? = null,
    
    @SerializedName("key")
    @Expose
    val key: String? = null,
    
    @SerializedName("authors")
    @Expose
    val authors: List<Author>? = null,
    
    @SerializedName("publish_date")
    @Expose
    val publishDate: String? = null,
    
    @SerializedName("excerpts")
    @Expose
    val excerpts: List<Excerpt>? = null
)