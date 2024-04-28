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

package com.atharok.barcodescanner.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.atharok.barcodescanner.domain.entity.customUrl.CustomUrl

@Dao
interface CustomUrlDao {

    @Query("SELECT * FROM CustomUrl ORDER BY name")
    fun getCustomUrlList(): LiveData<List<CustomUrl>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(customUrl: CustomUrl): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(customUrls: List<CustomUrl>)

    @Query("UPDATE CustomUrl SET name = :name, url = :url WHERE id = :id")
    suspend fun update(id: Int, name: String, url: String): Int

    @Query("DELETE FROM CustomUrl")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun deleteCustomUrls(customUrls: List<CustomUrl>): Int

    @Delete
    suspend fun delete(customUrl: CustomUrl): Int
}