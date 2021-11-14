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
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.databinding.ActivityBarcodeCreatorFormsBinding
import com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.FormCreateBarcodeHeaderFragment
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.domain.library.BarcodeFormatChecker
import com.atharok.barcodescanner.domain.library.BarcodeFormatCheckerResult.CheckerResponse
import com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.forms.AbstractFormCreateBarcodeFragment
import com.google.zxing.BarcodeFormat
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * Activity contenant les formulaires de créations de code-barres. Il contient un Fragment
 * contenant le formulaire. Le Fragment est choisie en fonction du type de code-barre choisie via
 * l'Intent.
 */
class BarcodeCreatorFormsActivity : BaseActivity() {

    private lateinit var formCreateBarcodeFragment: AbstractFormCreateBarcodeFragment

    private val myScope = getKoin().getOrCreateScope(
        BARCODE_CREATOR_SCOPE_SESSION_ID,
        named(BARCODE_CREATOR_SCOPE_SESSION)
    )

    private val viewBinding: ActivityBarcodeCreatorFormsBinding by lazy { ActivityBarcodeCreatorFormsBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        setSupportActionBar(viewBinding.activityFormCreateBarcodeToolbar.toolbar)

        val allBarCodeCreatorType = intent.getSerializableExtra(BARCODE_TYPE_ENUM_KEY) as BarcodeFormatDetails?

        if(allBarCodeCreatorType != null) {
            configureHeaderFragment(allBarCodeCreatorType)
            configureFormFragment(allBarCodeCreatorType)
        }
    }

    override fun onDestroy() {
        myScope.close()
        super.onDestroy()
    }

    // ---- Header ----

    /**
     * Configure le Fragment contenant le Header de l'Activity.
     */
    private fun configureHeaderFragment(barcodeFormatDetails: BarcodeFormatDetails){
        val arguments = Bundle().apply {
            putSerializable(BARCODE_TYPE_ENUM_KEY, barcodeFormatDetails)
        }

        applyFragment(
            viewBinding.activityFormCreateBarcodeHeaderFrameLayout.id,
            FormCreateBarcodeHeaderFragment::class,
            arguments
        ) // Le fragment est placé dans le FrameLayout prévue à cet effet
    }

    // ---- Formulaire ----

    /**
     * Configure le Fragment contenant le formulaire de création.
     */
    private fun configureFormFragment(barcodeFormatDetails: BarcodeFormatDetails){

        formCreateBarcodeFragment = myScope.get<AbstractFormCreateBarcodeFragment> {
            parametersOf(barcodeFormatDetails)
        }

        formCreateBarcodeFragment.allowEnterTransitionOverlap = false
        formCreateBarcodeFragment.allowReturnTransitionOverlap = false

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            //setCustomAnimations(R.anim.enter_transition, R.anim.exit_transition, R.anim.enter_transition, R.anim.exit_transition)
            replace(viewBinding.activityFormCreateBarcodeFormFragment.id, formCreateBarcodeFragment)
        }
    }

    override fun onBackPressed() {
        formCreateBarcodeFragment.closeVirtualKeyBoard(viewBinding.root)

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
            //setCustomAnimations(R.anim.enter_transition, R.anim.exit_transition)
            remove(formCreateBarcodeFragment)
        }
        super.onBackPressed()
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
        val content = formCreateBarcodeFragment.generateBarcodeTextFromForm()

        if(content!="") {

            val allBarCodeCreatorType = intent.getSerializableExtra(BARCODE_TYPE_ENUM_KEY) as BarcodeFormatDetails?

            val barCodeFormat: BarcodeFormat = allBarCodeCreatorType?.format ?: BarcodeFormat.QR_CODE

            val checkerManager: BarcodeFormatChecker = get()

            val response = checkerManager.check(content, barCodeFormat)

            when (response.response) {
                CheckerResponse.BAR_CODE_SUCCESSFUL -> startBarCodeGeneratorResultActivity(content, barCodeFormat)
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

    private fun startBarCodeGeneratorResultActivity(content: String, barCodeFormat: BarcodeFormat){
        viewBinding.activityFormCreateBarcodeErrorLayout.visibility = View.GONE
        viewBinding.activityFormCreateBarcodeErrorTextView.text = ""

        val intent = Intent(this, BarcodeDetailsActivity::class.java).apply {
            putExtra(BARCODE_CONTENTS_KEY, content)
            putExtra(BARCODE_FORMAT_KEY, barCodeFormat.name)
        }

        startActivity(intent)
    }

    private fun configureErrorMessage(message: String) {
        viewBinding.activityFormCreateBarcodeErrorLayout.visibility = View.VISIBLE
        viewBinding.activityFormCreateBarcodeErrorTextView.text = message
    }
}