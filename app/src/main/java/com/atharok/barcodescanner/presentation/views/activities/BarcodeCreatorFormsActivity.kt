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
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_CONTENTS_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_FORMAT_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_TYPE_ENUM_KEY
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.databinding.ActivityBarcodeCreatorFormsBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.domain.library.BarcodeFormatChecker
import com.atharok.barcodescanner.domain.library.BarcodeFormatCheckerResult.CheckerResponse
import com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.AbstractFormCreateBarcodeFragment
import com.google.zxing.BarcodeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * Activity contenant les formulaires de créations de code-barres. Il contient un Fragment
 * contenant le formulaire. Le Fragment est choisie en fonction du type de code-barre choisie via
 * l'Intent.
 */
class BarcodeCreatorFormsActivity : BaseActivity() {

    private var formCreateBarcodeFragment: AbstractFormCreateBarcodeFragment? = null

    private val allBarcodeFormat: BarcodeFormatDetails? by lazy {
        intent.serializable(BARCODE_TYPE_ENUM_KEY, BarcodeFormatDetails::class.java)
    }

    private val viewBinding: ActivityBarcodeCreatorFormsBinding by lazy { ActivityBarcodeCreatorFormsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeCreatorFormsToolbar.toolbar)

        lifecycleScope.launch(Dispatchers.Main) {
            allBarcodeFormat?.apply(::configureHeader)
            allBarcodeFormat?.apply(::configureFormFragment)
        }

        // onBackPressed
        onBackPressedDispatcher.addCallback(this) {

            formCreateBarcodeFragment?.let { fragment ->
                fragment.closeVirtualKeyBoard(viewBinding.root)
                supportFragmentManager.commit {
                    setReorderingAllowed(true)
                    setCustomAnimations(R.anim.barcode_creator_enter, R.anim.barcode_creator_exit)
                    remove(fragment)
                }
            }

            finishAfterTransition()
        }

        setContentView(viewBinding.root)
    }

    // ---- Header ----

    /**
     * Configure le Fragment contenant le Header de l'Activity.
     */
    private fun configureHeader(barcodeFormatDetails: BarcodeFormatDetails){

        val imageView = viewBinding.activityBarcodeCreatorFormsHeader.templateItemBarcodeCreatorImageView
        val textView = viewBinding.activityBarcodeCreatorFormsHeader.templateItemBarcodeCreatorTextView

        textView.text = getString(barcodeFormatDetails.stringResource)
        imageView.setImageResource(barcodeFormatDetails.drawableResource)
    }

    // ---- Formulaire ----

    /**
     * Configure le Fragment contenant le formulaire de création.
     */
    private fun configureFormFragment(barcodeFormatDetails: BarcodeFormatDetails){

        formCreateBarcodeFragment = get<AbstractFormCreateBarcodeFragment> {
            parametersOf(barcodeFormatDetails)
        }

        formCreateBarcodeFragment?.let { fragment ->
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(R.anim.barcode_creator_enter, R.anim.barcode_creator_exit)
                replace(viewBinding.activityBarcodeCreatorFormsFragment.id, fragment)
            }
        }
    }

    // ---- Menu contenant l'item permettant de générer le QrCode ----

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_confirm, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.menu_activity_confirm_item -> checkFormat()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun checkFormat(){
        val content = formCreateBarcodeFragment?.generateBarcodeTextFromForm()

        if(!content.isNullOrBlank()) {

            val barcodeFormat: BarcodeFormat = allBarcodeFormat?.format ?: BarcodeFormat.QR_CODE

            val checkerManager: BarcodeFormatChecker = get()

            val response = checkerManager.check(content, barcodeFormat)

            when (response.response) {
                CheckerResponse.BAR_CODE_SUCCESSFUL -> startBarcodeDetailsActivity(content, barcodeFormat)
                CheckerResponse.BAR_CODE_NOT_A_NUMBER_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_not_a_number_message))
                CheckerResponse.BAR_CODE_WRONG_LENGTH_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_wrong_length_message, response.length.toString()))
                CheckerResponse.BAR_CODE_WRONG_KEY_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_wrong_key_message, response.length.toString(), response.key.toString()))
                CheckerResponse.BAR_CODE_ENCODING_ISO_8859_1_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_encoding_iso_8859_1_error_message))
                CheckerResponse.BAR_CODE_ENCODING_US_ASCII_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_encoding_us_ascii_error_message))
                CheckerResponse.BAR_CODE_CODE_93_REGEX_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_93_regex_error_message))
                CheckerResponse.BAR_CODE_CODE_39_REGEX_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_39_regex_error_message))
                CheckerResponse.BAR_CODE_CODABAR_REGEX_ERROR -> configureErrorMessage(getString(R.string.error_bar_codabar_regex_error_message))
                CheckerResponse.BAR_CODE_ITF_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_itf_error_message))
                CheckerResponse.BAR_CODE_UPC_E_NOT_START_WITH_0_ERROR -> configureErrorMessage(getString(R.string.error_bar_code_upc_e_not_start_with_0_error_message))
            }
        } else {
            configureErrorMessage(getString(R.string.error_bar_code_none_character_message))
        }
    }

    private fun startBarcodeDetailsActivity(content: String, barCodeFormat: BarcodeFormat){
        viewBinding.activityBarcodeCreatorFormsErrorLayout.visibility = View.GONE
        viewBinding.activityBarcodeCreatorFormsErrorTextView.text = ""

        val intent = getStartBarcodeDetailsActivityIntent().apply {
            putExtra(BARCODE_CONTENTS_KEY, content)
            putExtra(BARCODE_FORMAT_KEY, barCodeFormat.name)
        }

        startActivity(intent)
    }

    private fun getStartBarcodeDetailsActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(BarcodeDetailsActivity::class) }

    private fun configureErrorMessage(message: String) {
        viewBinding.activityBarcodeCreatorFormsErrorLayout.visibility = View.VISIBLE
        viewBinding.activityBarcodeCreatorFormsErrorTextView.text = message
    }
}