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

package com.atharok.barcodescanner.data.network

import android.content.Context
import com.atharok.barcodescanner.BuildConfig
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

private const val WS_CALL_TIMEOUT_SECONDS = 10L
private const val CACHE_MAX_SIZE = 10L * 1024L * 1024L
private const val USER_AGENT = "${BuildConfig.APPLICATION_ID}/${BuildConfig.VERSION_NAME} (https://gitlab.com/Atharok/BarcodeScanner)"

fun createApiClient(context: Context, baseUrl: String): Retrofit =
    Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createHttpClient(context))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

private fun createHttpClient(context: Context): OkHttpClient =
    OkHttpClient.Builder()
        .addNetworkInterceptor(UserAgentInterceptor(USER_AGENT))
        .cache(createHttpCache(context))
        .addNetworkInterceptor(CacheInterceptor())
        .addInterceptor(WSApiInterceptor())
        .callTimeout(WS_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .build()

private fun createHttpCache(context: Context): Cache =
    Cache(File(context.cacheDir, "http-cache"), CACHE_MAX_SIZE)