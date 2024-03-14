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

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.commit
import androidx.lifecycle.lifecycleScope
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_TYPE_ENUM_KEY
import com.atharok.barcodescanner.databinding.ActivityBarcodeFormCreatorBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.presentation.views.fragments.barcodeFormCreator.AbstractBarcodeFormCreatorFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

/**
 * Activity contenant les formulaires de créations de code-barres. Il contient un Fragment
 * contenant le formulaire. Le Fragment est choisie en fonction du type de code-barre choisie via
 * l'Intent.
 */
class BarcodeFormCreatorActivity : BaseActivity() {

    private var formCreateBarcodeFragment: AbstractBarcodeFormCreatorFragment? = null

    private val allBarcodeFormat: BarcodeFormatDetails? by lazy {
        intent.serializable(BARCODE_TYPE_ENUM_KEY, BarcodeFormatDetails::class.java)
    }

    private val viewBinding: ActivityBarcodeFormCreatorBinding by lazy { ActivityBarcodeFormCreatorBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(viewBinding.activityBarcodeFormCreatorActivityLayout.toolbar)

        lifecycleScope.launch(Dispatchers.Main) {
            allBarcodeFormat?.apply(::configureHeader)
            allBarcodeFormat?.apply(::configureFormFragment)
        }

        // onBackPressed
        onBackPressedDispatcher.addCallback(this) {

            formCreateBarcodeFragment?.let { fragment ->
                fragment.hideSoftKeyboard()
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
    private fun configureHeader(barcodeFormatDetails: BarcodeFormatDetails) {
        val imageView = viewBinding.activityBarcodeFormCreatorHeader.templateItemBarcodeCreatorImageView
        val textView = viewBinding.activityBarcodeFormCreatorHeader.templateItemBarcodeCreatorTextView
        textView.text = getString(barcodeFormatDetails.stringResource)
        imageView.setImageResource(barcodeFormatDetails.drawableResource)
    }

    // ---- Formulaire ----

    /**
     * Configure le Fragment contenant le formulaire de création.
     */
    private fun configureFormFragment(barcodeFormatDetails: BarcodeFormatDetails){
        formCreateBarcodeFragment = get<AbstractBarcodeFormCreatorFragment> {
            parametersOf(barcodeFormatDetails)
        }
        formCreateBarcodeFragment?.let { fragment ->
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                setCustomAnimations(R.anim.barcode_creator_enter, R.anim.barcode_creator_exit)
                replace(viewBinding.activityBarcodeFormCreatorFragment.id, fragment)
            }
        }
    }

    fun configureErrorMessage(message: String) {
        viewBinding.activityBarcodeFormCreatorErrorLayout.visibility = View.VISIBLE
        viewBinding.activityBarcodeFormCreatorErrorTextView.text = message
    }

    fun hideErrorMessage() {
        viewBinding.activityBarcodeFormCreatorErrorLayout.visibility = View.GONE
        viewBinding.activityBarcodeFormCreatorErrorTextView.text = ""
    }
}