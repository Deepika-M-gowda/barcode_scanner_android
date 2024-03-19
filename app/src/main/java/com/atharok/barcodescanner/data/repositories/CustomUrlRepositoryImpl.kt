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

package com.atharok.barcodescanner.data.repositories

import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.data.database.CustomUrlDao
import com.atharok.barcodescanner.domain.entity.customUrl.CustomUrl
import com.atharok.barcodescanner.domain.repositories.CustomUrlRepository

class CustomUrlRepositoryImpl(private val customUrlDao: CustomUrlDao): CustomUrlRepository {

    override fun getCustomUrlList(): LiveData<List<CustomUrl>> = customUrlDao.getCustomUrlList()

    override suspend fun insertCustomUrl(customUrl: CustomUrl): Long = customUrlDao.insert(customUrl)

    override suspend fun updateCustomUrl(customUrl: CustomUrl): Int = customUrlDao.update(customUrl.id, customUrl.name, customUrl.url)

    override suspend fun deleteAllCustomUrl(): Int = customUrlDao.deleteAll()

    override suspend fun deleteCustomUrls(customUrls: List<CustomUrl>): Int = customUrlDao.deleteCustomUrls(customUrls)

    override suspend fun deleteCustomUrl(customUrl: CustomUrl): Int = customUrlDao.delete(customUrl)
}