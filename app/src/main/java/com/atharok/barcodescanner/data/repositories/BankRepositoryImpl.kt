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
import com.atharok.barcodescanner.data.database.BankDao
import com.atharok.barcodescanner.domain.entity.bank.Bank
import com.atharok.barcodescanner.domain.repositories.BankRepository

class BankRepositoryImpl(private val bankDao: BankDao): BankRepository {

    override fun getBankList(): LiveData<List<Bank>> = bankDao.getBankList()

    override suspend fun insertBank(bank: Bank): Long = bankDao.insert(bank)

    override suspend fun insertBanks(banks: List<Bank>) = bankDao.insert(banks)

    override suspend fun deleteAllBank(): Int = bankDao.deleteAll()

    override suspend fun deleteBanks(banks: List<Bank>): Int = bankDao.deleteBanks(banks)

    override suspend fun deleteBank(bank: Bank): Int = bankDao.delete(bank)
}