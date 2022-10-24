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

package com.atharok.barcodescanner.presentation.views.activities

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.atharok.barcodescanner.common.extensions.getParcelableExtraAppCompat
import com.atharok.barcodescanner.common.extensions.toIntent
import com.atharok.barcodescanner.common.utils.INTENT_PICK_IMAGE
import com.google.zxing.Result
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named


class BarcodeScanFromImageGalleryActivity: BarcodeScanFromImageAbstractActivity() {

    companion object {
        private const val URI_INTENT_KEY = "uriIntentKey"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val uri: Uri? = getImageUri()
        if(uri == null) pickImageFromGallery() else configureCropManagement(uri)
    }

    /**
     * Gère le retour de la galerie d'image.
     */
    private val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri: Uri? = result?.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null) {
                if (!intent.hasExtra(URI_INTENT_KEY))
                    intent.putExtra(URI_INTENT_KEY, uri)
                configureCropManagement(uri)
            } else {
                finish()
            }
        }

    /**
     * Prépare et ouvre la gallery pour récupérer une image.
     */
    private fun pickImageFromGallery(){
        val imagePickerIntent = get<Intent>(named(INTENT_PICK_IMAGE))
        resultLauncher.launch(imagePickerIntent)
    }

    /**
     * Permet de récupérer l'Uri via l'intent de l'Activity si elle a été stockée, évitant ainsi de
     * repasser par la gallery pour récupérer l'image (utile lors de la rotation de l'écran).
     */
    private fun getImageUri(): Uri? = if(intent.hasExtra(URI_INTENT_KEY)) {
        intent.getParcelableExtraAppCompat(URI_INTENT_KEY, Uri::class.java)
    } else null


    override fun onSuccessfulImageScan(result: Result?) {
        setResult(Activity.RESULT_OK, result?.toIntent())
        finish()
    }
}