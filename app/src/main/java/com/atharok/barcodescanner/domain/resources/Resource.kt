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

package com.atharok.barcodescanner.domain.resources

/** Resource to map data with its loading state. */
sealed class Resource<T> {
    /** Progress status. */
    data class Progress<T>(val data: T? = null, val tag: String? = null) : Resource<T>()

    /** Success status. */
    data class Success<T>(val data: T, val tag: String? = null) : Resource<T>()

    /** Failure status. */
    data class Failure<T>(val throwable: Throwable, val data: T, val tag: String? = null) : Resource<T>()

    companion object {
        /** Get a [Resource] in loading state with provided [data]. */
        fun <T> loading(data: T? = null, tag: String? = null): Resource<T> = Progress(data, tag)

        /** Get a [Resource] in success state with provided [data]. */
        fun <T> success(data: T, tag: String? = null): Success<T> = Success(data, tag)

        /** Get a [Resource] in failure state with provided [throwable]. */
        fun <T> failure(throwable: Throwable, data: T, tag: String? = null): Resource<T> =
            Failure(throwable, data, tag)
    }
}