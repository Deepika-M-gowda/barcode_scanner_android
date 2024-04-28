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

package com.atharok.barcodescanner.domain.usecases

import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.domain.entity.bank.Bank
import com.atharok.barcodescanner.domain.repositories.BankRepository

class DatabaseBankUseCase(private val bankRepository: BankRepository) {

    val bankList: LiveData<List<Bank>> = bankRepository.getBankList()

    suspend fun insertBank(bank: Bank): Long = bankRepository.insertBank(bank)

    suspend fun insertBanks(banks: List<Bank>) = bankRepository.insertBanks(banks)

    suspend fun deleteBank(bank: Bank) = bankRepository.deleteBank(bank)

    suspend fun deleteBanks(banks: List<Bank>) = bankRepository.deleteBanks(banks)

    suspend fun deleteAll(): Int = bankRepository.deleteAllBank()
}