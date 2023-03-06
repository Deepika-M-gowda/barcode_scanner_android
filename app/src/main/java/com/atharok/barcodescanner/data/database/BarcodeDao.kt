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
import androidx.room.*
import com.atharok.barcodescanner.domain.entity.barcode.Barcode

@Dao
interface BarcodeDao {

    @Query("SELECT * FROM Barcode ORDER BY scan_date DESC")
    fun getBarcodeList(): LiveData<List<Barcode>>

    @Query("SELECT * FROM Barcode WHERE scan_date = :date LIMIT 1")
    fun getBarcodeByDate(date: Long): LiveData<Barcode>

    @Insert
    suspend fun insert(barcode: Barcode): Long

    @Query("UPDATE Barcode SET type = :type WHERE scan_date = :date")
    suspend fun updateType(date: Long, type: String): Int

    @Query("UPDATE Barcode SET name = :name WHERE scan_date = :date")
    suspend fun updateName(date: Long, name: String): Int

    @Query("UPDATE Barcode SET type = :type, name = :name WHERE scan_date = :date")
    suspend fun updateTypeAndName(date: Long, type: String, name: String): Int

    @Query("DELETE FROM Barcode")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun deleteBarcodes(barcodes: List<Barcode>): Int

    @Delete
    suspend fun delete(barcode: Barcode): Int
}