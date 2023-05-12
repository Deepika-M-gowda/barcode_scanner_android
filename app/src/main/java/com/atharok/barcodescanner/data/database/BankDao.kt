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
import com.atharok.barcodescanner.domain.entity.bank.Bank

@Dao
interface BankDao {

    @Query("SELECT * FROM Bank ORDER BY name")
    fun getBankList(): LiveData<List<Bank>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(bank: Bank): Long

    @Query("DELETE FROM Bank")
    suspend fun deleteAll(): Int

    @Delete
    suspend fun deleteBanks(banks: List<Bank>): Int

    @Delete
    suspend fun delete(bank: Bank): Int
}