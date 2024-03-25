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

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.getDisplayName
import com.atharok.barcodescanner.common.extensions.parcelable
import com.atharok.barcodescanner.common.extensions.read
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_BACKGROUND_COLOR_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_CORNER_RADIUS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_DEFAULT_SIZE
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_FRONT_COLOR_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_HEIGHT_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_WIDTH_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.common.utils.showSimpleDialog
import com.atharok.barcodescanner.databinding.ActivityBarcodeDetailsBinding
import com.atharok.barcodescanner.domain.entity.ImageFormat
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.presentation.intent.createActionCreateImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.viewmodel.ImageManagerViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeImageEditor.BarcodeImageEditorFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeImageEditor.BarcodeImageFragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import ezvcard.Ezvcard
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date

class BarcodeDetailsActivity : BaseActivity() {

    private val imageManagerViewModel: ImageManagerViewModel by viewModel()

    private val viewBinding: ActivityBarcodeDetailsBinding by lazy { ActivityBarcodeDetailsBinding.inflate(layoutInflater) }

    private val contents: String by lazy {
        getIntentStringValue() ?: error()
    }

    private var alertDialog: AlertDialog? = null

    private fun error(): String {
        Toast.makeText(this, getString(R.string.scan_error_exception_label, "Barcode contents (String) is missing"), Toast.LENGTH_LONG).show()
        return "ERROR"
    }

    private val format: BarcodeFormat by lazy {
        getBarcodeFormat()
    }

    private val qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel by lazy {
        getQrCodeErrorCorrectionLevel(format)
    }

    private val properties: BarcodeImageGeneratorProperties by lazy {
        BarcodeImageGeneratorProperties(
            contents = contents,
            format = format,
            qrCodeErrorCorrectionLevel = qrCodeErrorCorrectionLevel,
            size = BARCODE_IMAGE_DEFAULT_SIZE,
            frontColor = Color.BLACK,
            backgroundColor = Color.WHITE
        )
    }

    private val barcodeImageFragment: BarcodeImageFragment by lazy {
        BarcodeImageFragment.newInstance(properties)
    }

    private val barcodeImageEditorFragment: BarcodeImageEditorFragment by lazy {
        BarcodeImageEditorFragment.newInstance(properties)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeDetailsActivityLayout.toolbar)
        supportActionBar?.title = format.getDisplayName(this)

        savedInstanceState?.let {
            properties.apply {
                frontColor = it.getInt(BARCODE_IMAGE_FRONT_COLOR_KEY, properties.frontColor)
                backgroundColor = it.getInt(BARCODE_IMAGE_BACKGROUND_COLOR_KEY, properties.backgroundColor)
                cornerRadius = it.getFloat(BARCODE_IMAGE_CORNER_RADIUS_KEY, properties.cornerRadius)
                width = it.getInt(BARCODE_IMAGE_WIDTH_KEY, properties.width)
                height = it.getInt(BARCODE_IMAGE_HEIGHT_KEY, properties.height)
            }
        }

        replaceFragment(
            containerViewId = viewBinding.activityBarcodeDetailsImageLayout.id,
            fragment = barcodeImageFragment,
        )

        replaceFragment(
            containerViewId = viewBinding.activityBarcodeDetailsSettingsLayout.id,
            fragment = barcodeImageEditorFragment,
        )

        setContentView(viewBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(BARCODE_IMAGE_FRONT_COLOR_KEY, properties.frontColor)
        outState.putInt(BARCODE_IMAGE_BACKGROUND_COLOR_KEY, properties.backgroundColor)
        outState.putFloat(BARCODE_IMAGE_CORNER_RADIUS_KEY, properties.cornerRadius)
        outState.putInt(BARCODE_IMAGE_WIDTH_KEY, properties.width)
        outState.putInt(BARCODE_IMAGE_HEIGHT_KEY, properties.height)
        super.onSaveInstanceState(outState)
    }

    private fun getIntentStringValue(): String? {
        return if(intent?.action == Intent.ACTION_SEND){
            when (intent.type) {
                "text/plain" -> intent.getStringExtra(Intent.EXTRA_TEXT)
                "text/x-vcard" -> {
                    intent.parcelable(Intent.EXTRA_STREAM, Uri::class.java)?.read(this)?.let { vCardText: String ->
                        try {
                            Ezvcard.parse(vCardText).first()?.let { vCard ->
                                vCard.photos?.clear()
                                Ezvcard.write(vCard).prodId(false).go()
                            }
                        } catch (e: NoClassDefFoundError) {
                            showDialog(
                                titleRes = R.string.error,
                                message = getString(R.string.scan_error_exception_label, e.toString())
                            )
                            vCardText
                        } catch (e: Exception) {
                            showDialog(
                                titleRes = R.string.error,
                                message = getString(R.string.scan_error_exception_label, e.toString())
                            )
                            vCardText
                        }
                    }
                }
                "text/calendar" -> intent.parcelable(Intent.EXTRA_STREAM, Uri::class.java)?.read(this)
                else -> intent.getStringExtra(Intent.EXTRA_TEXT)
            }
        } else intent.getStringExtra(BARCODE_CONTENTS_KEY)
    }

    private fun getBarcodeFormat(): BarcodeFormat {
        val barcodeFormatString: String = intent.getStringExtra(BARCODE_FORMAT_KEY) ?: BarcodeFormat.QR_CODE.name
        return BarcodeFormat.valueOf(barcodeFormatString)
    }

    private fun getQrCodeErrorCorrectionLevel(barcodeFormat: BarcodeFormat): QrCodeErrorCorrectionLevel {
        return when(barcodeFormat) {
            BarcodeFormat.QR_CODE -> {
                val qrCodeErrorCorrectionLevelString: String? = intent.getStringExtra(QR_CODE_ERROR_CORRECTION_LEVEL_KEY)
                if(qrCodeErrorCorrectionLevelString!=null) {
                    QrCodeErrorCorrectionLevel.valueOf(qrCodeErrorCorrectionLevelString)
                } else {
                    settingsManager.getQrCodeErrorCorrectionLevel()
                }
            }
            else -> QrCodeErrorCorrectionLevel.NONE
        }
    }

    // ---- Barcode Bitmap Creator ----

    // Call by Fragments
    fun regenerateBitmap(
        width: Int = properties.width,
        height: Int = properties.height,
        @ColorInt frontColor: Int = properties.frontColor,
        @ColorInt backgroundColor: Int = properties.backgroundColor,
        cornerRadius: Float = properties.cornerRadius
    ) {
        barcodeImageFragment.generateNewBarcodeBitmap(
            properties.apply {
                this.width = width
                this.height = height
                this.frontColor = frontColor
                this.backgroundColor = backgroundColor
                this.cornerRadius = cornerRadius
            }
        )
    }

    // ---- Menu contenant les items permettant de sauvegarder ou partager le QrCode ----

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_barcode_details, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_activity_barcode_details_save_image_png -> startExportation(ImageFormat.PNG)
            R.id.menu_activity_barcode_details_save_image_jpg -> startExportation(ImageFormat.JPG)
            R.id.menu_activity_barcode_details_save_image_svg -> startExportation(ImageFormat.SVG)
            R.id.menu_activity_barcode_details_share_image -> shareImage()
            R.id.menu_activity_barcode_details_share_text -> shareText()
        }
        return super.onOptionsItemSelected(item)
    }

    // ---- Export image ----

    private val result: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            if(uri != null) {
                exportAndObserve(uri)
            }
        }

    private var imageFormat: ImageFormat = ImageFormat.PNG

    private fun startExportation(imageFormat: ImageFormat) {
        this.imageFormat = imageFormat
        val date = get<Date>()
        val simpleDateFormat = get<SimpleDateFormat> { parametersOf("yyyy-MM-dd-HH-mm-ss") }
        val dateNameStr = simpleDateFormat.format(date)
        val name = "barcode_$dateNameStr"

        val intent: Intent = createActionCreateImageIntent(name, imageFormat.mimeType)
        result.launch(intent)
    }

    private fun exportAndObserve(uri: Uri) {
        export(uri).observe(this) { response ->
            when(response) {
                is Resource.Progress -> {}
                is Resource.Success -> {
                    when(response.data) {
                        true -> show(R.string.snack_bar_message_save_bitmap_ok)
                        else-> show(R.string.snack_bar_message_save_bitmap_error)
                    }
                }
                is Resource.Failure -> show(R.string.snack_bar_message_save_bitmap_error)
            }
        }
    }

    private fun export(uri: Uri): LiveData<Resource<Boolean>> {
        return when(imageFormat) {
            ImageFormat.PNG -> imageManagerViewModel.exportAsPng(barcodeImageFragment.bitmap, uri)
            ImageFormat.JPG -> imageManagerViewModel.exportAsJpg(barcodeImageFragment.bitmap, uri)
            ImageFormat.SVG -> imageManagerViewModel.exportAsSvg(properties, uri)
        }
    }

    // ---- Share image / text ----

    private fun shareImage() {
        imageManagerViewModel.shareBitmap(barcodeImageFragment.bitmap).observe(this) {
            when(it) {
                is Resource.Progress -> {}
                is Resource.Success -> {
                    when(it.data) {
                        null -> show(R.string.snack_bar_message_share_bitmap_error)
                        else -> {
                            val intent: Intent = createShareImageIntent(applicationContext, it.data)
                            startActivity(intent)
                        }
                    }
                }
                is Resource.Failure -> show(R.string.snack_bar_message_share_bitmap_error)
            }
        }
    }

    private fun shareText() {
        val intent: Intent = createShareTextIntent(applicationContext, contents)
        startActivity(intent)
    }

    // ---- Snackbar ----

    private fun show(@StringRes stringRes: Int) =
        Snackbar.make(viewBinding.root, getString(stringRes), Snackbar.LENGTH_SHORT).show()

    // ---- AlertDialog ----

    private fun showDialog(@StringRes titleRes: Int, message: String) {
        alertDialog = showSimpleDialog(this, titleRes, message)
    }
}