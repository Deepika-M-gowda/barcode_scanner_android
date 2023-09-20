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

package com.atharok.barcodescanner.presentation.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.atharok.barcodescanner.domain.entity.FileFormat
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.domain.usecases.StorageManagerUseCase

class StorageManagerViewModel(private val fileUseCase: StorageManagerUseCase): ViewModel() {

    fun exportToFile(
        barcodes: List<Barcode>,
        format: FileFormat,
        uri: Uri
    ): LiveData<Resource<Boolean>> {
        return when(format) {
            FileFormat.CSV -> fileUseCase.exportToCsv(barcodes, uri)
            FileFormat.JSON -> fileUseCase.exportToJson(barcodes, uri)
        }
    }
}