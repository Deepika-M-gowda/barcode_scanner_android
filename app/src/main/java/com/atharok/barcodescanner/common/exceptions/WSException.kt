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

package com.atharok.barcodescanner.common.exceptions

import okhttp3.Response
import java.io.IOException
import java.net.HttpURLConnection

class WSException(val code: Int, val type: Type, message: String?): IOException(message) {
    constructor(response: Response): this(response.code, response.type(), response.message)

    enum class Type {
        NOT_FOUND,
        UNAUTHORISED,
        UPDATE_REQUIRED,
        FORBIDDEN,
        OTHER
    }

    override fun toString(): String {
        return "Error type: $type (Error $code)\n${super.toString()}"
    }

    companion object {
        private fun Response.type(): Type {
            return when(code){
                HttpURLConnection.HTTP_NOT_FOUND -> Type.NOT_FOUND
                HttpURLConnection.HTTP_UNAUTHORIZED -> Type.UNAUTHORISED
                426 -> Type.UPDATE_REQUIRED
                HttpURLConnection.HTTP_FORBIDDEN -> Type.FORBIDDEN
                else -> Type.OTHER
            }
        }
    }
}