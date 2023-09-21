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
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.extensions.getDisplayName
import com.atharok.barcodescanner.common.extensions.parcelable
import com.atharok.barcodescanner.common.extensions.read
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.common.utils.QR_CODE_ERROR_CORRECTION_LEVEL_KEY
import com.atharok.barcodescanner.databinding.ActivityBarcodeDetailsBinding
import com.atharok.barcodescanner.domain.entity.ImageFormat
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.presentation.intent.createActionCreateImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.viewmodel.ImageManagerViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisAboutFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ExpandableViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date

class BarcodeDetailsActivity : BaseActivity() {

    private val imageManagerViewModel: ImageManagerViewModel by viewModel()

    private val viewBinding: ActivityBarcodeDetailsBinding by lazy { ActivityBarcodeDetailsBinding.inflate(layoutInflater) }

    private var bitmap: Bitmap? = null
    private var contents: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeImageToolbar.toolbar)

        viewBinding.activityBarcodeImageOuterView.fixAnimateLayoutChangesInNestedScroll()

        val barcodeContents: String? = getIntentStringValue()
        val barcodeFormat: BarcodeFormat = getBarcodeFormat()
        val qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel = getQrCodeErrorCorrectionLevel(barcodeFormat)

        if(barcodeContents != null) {
            this.contents = barcodeContents
            createBarcodeBitmap(barcodeContents, barcodeFormat, qrCodeErrorCorrectionLevel)
            configureBarcodeInformation(barcodeContents, barcodeFormat, qrCodeErrorCorrectionLevel)
        }else{
            viewBinding.activityBarcodeImageAboutBarcodeEntitledLayout.visibility = View.GONE
        }

        supportActionBar?.title = barcodeFormat.getDisplayName(this)

        setContentView(viewBinding.root)
    }

    private fun getIntentStringValue(): String? {
        return if(intent?.action == Intent.ACTION_SEND){
            when (intent.type) {
                "text/plain" -> intent.getStringExtra(Intent.EXTRA_TEXT)
                "text/x-vcard" -> intent.parcelable(Intent.EXTRA_STREAM, Uri::class.java)?.read(this)
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
                if(qrCodeErrorCorrectionLevelString!=null){
                    QrCodeErrorCorrectionLevel.valueOf(qrCodeErrorCorrectionLevelString)
                }else{
                    settingsManager.getQrCodeErrorCorrectionLevel()
                }
            }
            else -> QrCodeErrorCorrectionLevel.NONE
        }
    }

    // ---- Barcode Bitmap Creator ----

    private fun createBarcodeBitmap(contents: String, barcodeFormat: BarcodeFormat, qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel) {
        imageManagerViewModel.createBitmap(
            contents,
            barcodeFormat,
            qrCodeErrorCorrectionLevel
        ).observe(this) {
            when(it) {
                is Resource.Progress -> viewBinding.activityBarcodeDetailsProgressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    bitmap = it.data
                    bitmap?.let { localBitmap ->
                        viewBinding.activityBarcodeImageImageView.setImageBitmap(localBitmap)
                    }
                    viewBinding.activityBarcodeDetailsProgressBar.visibility = View.GONE
                }
                is Resource.Failure -> {
                    viewBinding.activityBarcodeDetailsProgressBar.visibility = View.GONE
                }
            }
        }
    }

    // ---- Barcode Information ----

    private fun configureBarcodeInformation(contents: String, format: BarcodeFormat, qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel) {
        // Entitled
        viewBinding.activityBarcodeImageAboutBarcodeEntitledLayout.visibility = View.VISIBLE
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.activityBarcodeImageAboutBarcodeEntitledTextViewTemplate.root.text = entitled

        configureContentsExpandableViewFragment(contents, format)
        configureAboutBarcodeFragment(contents, format, qrCodeErrorCorrectionLevel)
    }

    private fun configureContentsExpandableViewFragment(contents: String, format: BarcodeFormat) {

        val iconResource: Int = when(format) {
            BarcodeFormat.QR_CODE -> R.drawable.baseline_qr_code_24
            BarcodeFormat.AZTEC -> R.drawable.ic_aztec_code_24
            BarcodeFormat.DATA_MATRIX -> R.drawable.ic_data_matrix_code_24
            BarcodeFormat.PDF_417 -> R.drawable.ic_pdf_417_code_24
            else -> R.drawable.ic_bar_code_24
        }

        val contentsFragment = ExpandableViewFragment.newInstance(
            title = getString(R.string.bar_code_content_label),
            contents = contents,
            drawableResource = iconResource
        )

        replaceFragment(
            containerViewId = viewBinding.activityBarcodeImageBarcodeContentsFrameLayout.id,
            fragment = contentsFragment
        )
    }

    private fun configureAboutBarcodeFragment(contents: String, format: BarcodeFormat, qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel) {

        val barcode: Barcode = get { parametersOf(contents, format.name, qrCodeErrorCorrectionLevel) }
        val barcodeAnalysis = DefaultBarcodeAnalysis(barcode)

        val args: Bundle = get<Bundle>().apply {
            putSerializable(PRODUCT_KEY, barcodeAnalysis)
        }

        replaceFragment(
            containerViewId = viewBinding.activityBarcodeImageAboutBarcodeFrameLayout.id,
            fragmentClass = BarcodeAnalysisAboutFragment::class,
            args = args
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
            ImageFormat.PNG -> {
                imageManagerViewModel.exportAsPng(bitmap, uri)
            }
            ImageFormat.JPG -> {
                imageManagerViewModel.exportAsJpg(bitmap, uri)
            }
            ImageFormat.SVG -> {
                val barcodeContents: String? = getIntentStringValue()
                val barcodeFormat: BarcodeFormat = getBarcodeFormat()
                val qrCodeErrorCorrectionLevel: QrCodeErrorCorrectionLevel = getQrCodeErrorCorrectionLevel(barcodeFormat)
                imageManagerViewModel.exportAsSvg(
                    contents = barcodeContents,
                    barcodeFormat = barcodeFormat,
                    qrCodeErrorCorrectionLevel = qrCodeErrorCorrectionLevel,
                    uri = uri
                )
            }
        }
    }

    // ---- Share image / text ----

    private fun shareImage() {
        imageManagerViewModel.shareBitmap(bitmap).observe(this) {
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
        contents?.let {
            val intent: Intent = createShareTextIntent(applicationContext, it)//get(named(INTENT_SHARE_TEXT)) { parametersOf(contents) }
            startActivity(intent)
        }
    }

    // ---- Snackbar ----

    private fun show(@StringRes stringRes: Int) =
        Snackbar.make(viewBinding.root, getString(stringRes), Snackbar.LENGTH_SHORT).show()
}