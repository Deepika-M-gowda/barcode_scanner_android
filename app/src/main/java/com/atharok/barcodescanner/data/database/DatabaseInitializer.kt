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

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.atharok.barcodescanner.common.utils.DATABASE_NAME

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS Bank (name TEXT NOT NULL, bic TEXT NOT NULL, iban TEXT NOT NULL PRIMARY KEY)"
        )
    }
}


fun createDatabase(context: Context): AppDatabase =
    Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .addMigrations(MIGRATION_1_2)
        .build()

fun createBarcodeDao(database: AppDatabase): BarcodeDao = database.barcodeDao()

fun createBankDao(database: AppDatabase): BankDao = database.bankDao()