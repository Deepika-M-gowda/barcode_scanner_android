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
import androidx.lifecycle.lifecycleScope
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.*
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_SIZE
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.ActivityBarcodeDetailsBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.domain.library.BitmapBarcodeGenerator
import com.atharok.barcodescanner.domain.library.BitmapRecorder
import com.atharok.barcodescanner.domain.library.BitmapSharer
import com.atharok.barcodescanner.presentation.intent.createActionCreateImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareImageIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisAboutFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ExpandableViewFragment
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*

class BarcodeDetailsActivity : BaseActivity() {

    private val bitmapBarcodeGenerator: BitmapBarcodeGenerator by inject()
    private val bitmapRecorder: BitmapRecorder by inject()
    private val bitmapSharer: BitmapSharer by inject()

    private val viewBinding: ActivityBarcodeDetailsBinding by lazy { ActivityBarcodeDetailsBinding.inflate(layoutInflater) }

    private var bitmap: Bitmap? = null
    private var contents: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeImageToolbar.toolbar)

        viewBinding.activityBarcodeImageOuterView.fixAnimateLayoutChangesInNestedScroll()

        val barcodeContents = getIntentStringValue()
        val barcodeFormatString: String = intent.getStringExtra(BARCODE_FORMAT_KEY) ?: BarcodeFormat.QR_CODE.name
        val barcodeFormat: BarcodeFormat = BarcodeFormat.valueOf(barcodeFormatString)
        if(barcodeContents != null) {
            this.contents = barcodeContents
            createBarcodeBitmap(barcodeContents, barcodeFormat)
            configureBarcodeInformation(barcodeContents, barcodeFormat)
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

    // ---- Barcode Bitmap Creator ----

    private fun createBarcodeBitmap(bitmapStr: String, barcodeFormat: BarcodeFormat){

        lifecycleScope.launch(Dispatchers.Main) {

            val bitmapCreated: Bitmap? = withContext(Dispatchers.IO){
                bitmapBarcodeGenerator.create(
                    text = bitmapStr,
                    barcodeFormat = barcodeFormat,
                    width = BARCODE_IMAGE_SIZE,
                    height = if(barcodeFormat.is2DBarcode()) BARCODE_IMAGE_SIZE else BARCODE_IMAGE_SIZE/2
                )
            }

            if (bitmapCreated != null)
                viewBinding.activityBarcodeImageImageView.setImageBitmap(bitmapCreated)

            bitmap = bitmapCreated
        }
    }

    // ---- Barcode Information ----

    private fun configureBarcodeInformation(contents: String, format: BarcodeFormat){

        // Entitled
        viewBinding.activityBarcodeImageAboutBarcodeEntitledLayout.visibility = View.VISIBLE
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.activityBarcodeImageAboutBarcodeEntitledTextViewTemplate.root.text = entitled

        configureContentsExpandableViewFragment(contents, format)
        configureAboutBarcodeFragment(contents, format)
    }

    private fun configureContentsExpandableViewFragment(contents: String, format: BarcodeFormat){

        val iconResource: Int = when(format){
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

    private fun configureAboutBarcodeFragment(contents: String, format: BarcodeFormat){

        val barcodeInformation = Barcode(contents, format.name, Date().time)
        val barcodeAnalysis = DefaultBarcodeAnalysis(barcodeInformation)

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

        when(item.itemId){
            R.id.menu_activity_barcode_details_save -> createFile()
            R.id.menu_activity_barcode_details_share_image -> shareImage()
            R.id.menu_activity_barcode_details_share_text -> shareText()
        }

        return super.onOptionsItemSelected(item)
    }

    // ---- Save image ----

    private val result: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val uri = it.data?.data
            if(uri != null) {
                saveImage(uri)
            }
        }

    private fun createFile() {
        val date = get<Date>()
        val simpleDateFormat = get<SimpleDateFormat> { parametersOf("yyyy-MM-dd-HH-mm-ss") }
        val dateNameStr = simpleDateFormat.format(date)
        val name = "barcode_$dateNameStr"

        val intent: Intent = createActionCreateImageIntent(name)
        result.launch(intent)
    }

    private fun saveImage(uri: Uri) {
        val mBitmap = bitmap
        if(mBitmap!=null) {

            lifecycleScope.launch(Dispatchers.IO) {
                val successful = bitmapRecorder.recordImage(mBitmap, uri)
                val stringRes: Int = if(successful)
                    R.string.snack_bar_message_save_bitmap_ok
                else
                    R.string.snack_bar_message_save_bitmap_error

                show(stringRes)
            }
        }
    }

    // ---- Share image / text ----

    private fun shareImage() {
        val mBitmap = bitmap
        if(mBitmap!=null) {
            lifecycleScope.launch {
                val uri: Uri? = bitmapSharer.share(mBitmap)

                if(uri == null)
                    show(R.string.snack_bar_message_share_bitmap_error)
                else{
                    val intent: Intent = createShareImageIntent(applicationContext, uri)//get(named(INTENT_SHARE_IMAGE)) { parametersOf(uri) }
                    startActivity(intent)
                }
            }
        }
    }

    private fun shareText(){
        contents?.let {
            val intent: Intent = createShareTextIntent(applicationContext, it)//get(named(INTENT_SHARE_TEXT)) { parametersOf(contents) }
            startActivity(intent)
        }
    }

    // ---- Snackbar ----

    private fun show(@StringRes stringRes: Int) =
        Snackbar.make(viewBinding.root, getString(stringRes), Snackbar.LENGTH_SHORT).show()
}