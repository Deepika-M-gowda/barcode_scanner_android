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
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.toIntent
import com.atharok.barcodescanner.common.extensions.getParcelableExtraAppCompat
import com.atharok.barcodescanner.databinding.ActivityBarcodeScanFromImageBinding
import com.atharok.barcodescanner.domain.library.BitmapBarcodeAnalyser
import com.atharok.barcodescanner.common.utils.INTENT_PICK_IMAGE
import com.google.zxing.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class BarcodeScanFromImageActivity: BaseActivity() {

    companion object {
        private const val URI_INTENT_KEY = "uriIntentKey"
    }

    private val bitmapBarcodeAnalyser: BitmapBarcodeAnalyser by inject()

    private var zxingResult: Result? = null
    private var menuVisibility = false

    private val viewBinding: ActivityBarcodeScanFromImageBinding by lazy {
        ActivityBarcodeScanFromImageBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeScanFromImageToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewBinding.activityBarcodeScanFromImageCropImageView.clearImage()

        setContentView(viewBinding.root)

        val uri: Uri? = getImageUri()
        if(uri == null) pickImageFromGallery() else configureCropManagement(uri)
    }

    // ---- Menu ----

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_confirm, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menu_activity_confirm_item -> sendResultToAppIntent()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if(menu != null) {
            for (i in 0 until menu.size()) {
                menu.getItem(i).isVisible = menuVisibility
                menu.getItem(i).isEnabled = menuVisibility
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private fun setMenuVisibility(visible: Boolean){
        if(menuVisibility!=visible) {
            menuVisibility=visible
            invalidateOptionsMenu()
        }
    }

    // ---- Image Picker ----

    /**
     * Permet de récupérer l'Uri via l'intent de l'Activity si elle a été stockée, évitant ainsi de
     * repasser par la gallery pour récupérer l'image.
     */
    private fun getImageUri(): Uri?  = when {
        // Si l'URI a déjà été chargé (utile lors de la rotation de l'écran)
        intent.hasExtra(URI_INTENT_KEY) -> intent.getParcelableExtraAppCompat(URI_INTENT_KEY, Uri::class.java)

        // Si on récupère l'URI via un partage d'image d'une autre application (intent-filter)
        intent?.action == Intent.ACTION_SEND -> intent.getParcelableExtraAppCompat(Intent.EXTRA_STREAM, Uri::class.java)

        else -> null
    }

    /**
     * Gère le retour de la galerie d'image.
     */
    private val resultLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri: Uri? = result?.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null)
                configureCropManagement(uri)
            else
                finish()
        }

    /**
     * Prépare et ouvre la gallery pour récupérer une image.
     */
    private fun pickImageFromGallery(){
        val imagePickerIntent = get<Intent>(named(INTENT_PICK_IMAGE))
        resultLauncher.launch(imagePickerIntent)
    }

    // ---- Configure Crop ----

    /**
     * Configure tous les éléments utiles à la détection de code-barres dans l'image.
     * Le composant CropImageView permet de rogner l'image. Il permet donc d'analyser une partie
     * précise de l'image.
     */
    private fun configureCropManagement(uri: Uri){

        if(!intent.hasExtra(URI_INTENT_KEY))
            intent.putExtra(URI_INTENT_KEY, uri)

        // Insère l'image dans l'ImageCropView
        viewBinding.activityBarcodeScanFromImageCropImageView.setImageUriAsync(uri)

        var job: Job? = null

        // S'active à chaque appel de la méthode "imageCropView.getCroppedImageAsync()"
        viewBinding.activityBarcodeScanFromImageCropImageView.setOnCropImageCompleteListener { _, result ->
            val bitmap = result.getBitmap(this)

            if(bitmap != null){
                job?.cancel()
                job = lifecycleScope.launch(Dispatchers.IO) {
                    zxingResult = bitmapBarcodeAnalyser.findBarcodeInBitmap(bitmap)
                    setMenuVisibility(zxingResult != null)
                }
            }
        }

        // S'active lorsque l'image a fini de se charger
        viewBinding.activityBarcodeScanFromImageCropImageView.setOnSetImageUriCompleteListener { _, _, _ ->
            viewBinding.activityBarcodeScanFromImageCropImageView.croppedImageAsync()
        }

        // S'active lors du déplacement de l'overlay
        viewBinding.activityBarcodeScanFromImageCropImageView.setOnSetCropOverlayMovedListener {
            viewBinding.activityBarcodeScanFromImageCropImageView.croppedImageAsync()
        }
    }

    /**
     * Permet de revenir à MainActivity (et MainScannerFragment) avec le résultat du scan de l'image.
     */
    private fun sendResultToAppIntent() {

        setResult(Activity.RESULT_OK, zxingResult?.toIntent())
        finish()
    }
}