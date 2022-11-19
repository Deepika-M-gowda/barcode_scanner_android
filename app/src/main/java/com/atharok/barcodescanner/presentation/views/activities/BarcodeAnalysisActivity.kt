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

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION_ID
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.common.utils.IGNORE_USE_SEARCH_ON_API_SETTING_KEY
import com.atharok.barcodescanner.databinding.ActivityBarcodeAnalysisBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.product.ApiError
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.BookBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ProductViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root.DefaultBarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.ProductAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.bookProduct.BookAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.FoodAnalysisFragment
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import kotlin.reflect.KClass

class BarcodeAnalysisActivity: BaseActivity() {

    // ---- Views ----

    private val viewBinding: ActivityBarcodeAnalysisBinding by lazy {
        ActivityBarcodeAnalysisBinding.inflate(layoutInflater)
    }

    // ---- Scope ----

    private val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    )

    // ---- ViewModel ----

    private val databaseViewModel by viewModel<DatabaseViewModel>()
    private val retrofitViewModel by viewModel<ProductViewModel>()

    // ----

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.activityBarcodeInformationProgressBar.visibility = View.VISIBLE

        setSupportActionBar(viewBinding.activityBarcodeInformationToolbar.toolbar)

        val barcode: Barcode? = intent?.serializable(BARCODE_KEY, Barcode::class.java)

        if(barcode!=null){
            configureContentsView(barcode)
        } else {
            viewBinding.activityBarcodeInformationProgressBar.visibility = View.GONE
        }

        setContentView(viewBinding.root)
    }

    override fun onDestroy() {
        barcodeAnalysisScope.close()
        super.onDestroy()
    }

    private fun configureContentsView(barcode: Barcode){

        changeToolbarText(barcode)

        when {
            barcode.is1DProductBarcodeFormat -> configureApiResearch(barcode)
            barcode.is1DIndustrialBarcodeFormat -> configureDefaultBarcodeAnalysisView(DefaultBarcodeAnalysis(barcode), BarcodeType.INDUSTRIAL)
            barcode.is2DBarcodeFormat -> configureMatrixCodeView(DefaultBarcodeAnalysis(barcode))
            else -> configureDefaultBarcodeAnalysisView(DefaultBarcodeAnalysis(barcode), BarcodeType.UNKNOWN)
        }
    }

    fun restartApiResearch() {
        viewBinding.activityBarcodeInformationProgressBar.visibility = View.VISIBLE

        val barcode: Barcode? = intent?.serializable(BARCODE_KEY, Barcode::class.java)

        if(barcode!=null){
            configureApiResearch(barcode)
        } else {
            viewBinding.activityBarcodeInformationProgressBar.visibility = View.GONE
        }
    }

    private fun configureApiResearch(barcode: Barcode){

        val type = when(barcode.getBarcodeType()){
            BarcodeType.BOOK, BarcodeType.FOOD, BarcodeType.BEAUTY, BarcodeType.PET_FOOD -> barcode.getBarcodeType()
            else -> if(barcode.isBookBarcode()) BarcodeType.BOOK else BarcodeType.UNKNOWN_PRODUCT
        }

        val ignoreUseSearchOnApiSetting = intent.getBooleanExtra(IGNORE_USE_SEARCH_ON_API_SETTING_KEY, false)

        when {
            !checkInternetPermission() -> configureProductAnalysisView(DefaultBarcodeAnalysis(barcode), type, ApiError.NO_INTERNET_PERMISSION, getString(R.string.no_internet_permission))
            settingsManager.useSearchOnApi || ignoreUseSearchOnApiSetting -> observeOnAPI(barcode, type)
            else -> configureProductAnalysisView(DefaultBarcodeAnalysis(barcode), type, ApiError.NO_API_RESEARCH)
        }
    }

    private fun checkInternetPermission(): Boolean {
        val permission: Int = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
        return permission == PackageManager.PERMISSION_GRANTED
    }

    // ---- OpenFoodFacts ----
    private fun observeOnAPI(barcode: Barcode, defaultBarcodeType: BarcodeType){

        retrofitViewModel.getProduct(barcode).observe(this) {

            when (it) {

                is Resource.Progress -> {}

                is Resource.Failure -> configureProductAnalysisView(
                    barcodeAnalysis = DefaultBarcodeAnalysis(barcode),
                    barcodeType = defaultBarcodeType,
                    apiError = ApiError.ERROR,
                    message = it.throwable.toString()
                )

                is Resource.Success -> {
                    when (it.data) {
                        is FoodBarcodeAnalysis -> configureFoodAnalysisView(it.data)
                        is BookBarcodeAnalysis -> configureBookAnalysisView(it.data)
                        is DefaultBarcodeAnalysis -> configureProductAnalysisView(it.data, defaultBarcodeType, ApiError.NO_RESULT)
                        else -> configureProductAnalysisView(DefaultBarcodeAnalysis(barcode), defaultBarcodeType, ApiError.NO_RESULT)
                    }
                }

                // Si le code-barres n'a été trouvé sur aucun des services distants
                else -> configureProductAnalysisView(DefaultBarcodeAnalysis(barcode), defaultBarcodeType, ApiError.NO_RESULT)
            }
        }
    }

    // ---- Configuration de la vue principale en fonction du type de code-barres / produits ----
    
    private fun configureBookAnalysisView(
        barcodeAnalysis: BookBarcodeAnalysis
    ) = configureContentFragment(
        fragment = BookAnalysisFragment.newInstance(barcodeAnalysis),
        barcodeAnalysis = barcodeAnalysis,
        barcodeType = barcodeAnalysis.source.barcodeType
    )

    private fun configureFoodAnalysisView(
        barcodeAnalysis: FoodBarcodeAnalysis
    ) = configureContentFragment(
        fragment = FoodAnalysisFragment.newInstance(barcodeAnalysis),
        barcodeAnalysis = barcodeAnalysis,
        barcodeType = barcodeAnalysis.source.barcodeType
    )

    private fun configureProductAnalysisView(
        barcodeAnalysis: DefaultBarcodeAnalysis,
        barcodeType: BarcodeType,
        apiError: ApiError,
        message: String? = null
    ) = configureContentFragment(
        fragment = ProductAnalysisFragment.newInstance(barcodeAnalysis, apiError, message),
        barcodeAnalysis = barcodeAnalysis,
        barcodeType = barcodeType
    )

    private fun configureMatrixCodeView(barcodeAnalysis: DefaultBarcodeAnalysis){

        val barcodeType = barcodeAnalysisScope.get<BarcodeType> {
            parametersOf(barcodeAnalysis.barcode.contents, barcodeAnalysis.barcode.getBarcodeFormat())
        }

        configureDefaultBarcodeAnalysisView(barcodeAnalysis, barcodeType)
    }

    private fun configureDefaultBarcodeAnalysisView(
        barcodeAnalysis: DefaultBarcodeAnalysis,
        barcodeType: BarcodeType
    ) = configureContentFragment(
        fragment = DefaultBarcodeAnalysisFragment.newInstance(barcodeAnalysis),
        barcodeAnalysis = barcodeAnalysis,
        barcodeType = barcodeType
    )

    private fun configureContentFragment(fragment: Fragment, barcodeAnalysis: BarcodeAnalysis, barcodeType: BarcodeType) {

        updateTypeIntoDatabase(barcodeAnalysis = barcodeAnalysis, newBarcodeType = barcodeType)

        replaceFragment(
            containerViewId = viewBinding.activityBarcodeInformationContent.id,
            fragment = fragment
        )
        configureActionFragment(barcodeAnalysis.barcode)

        // Change le texte de la toolbar
        changeToolbarText(barcodeAnalysis.barcode)

        viewBinding.activityBarcodeInformationProgressBar.visibility = View.GONE
    }

    // ---- Actions ----

    private fun configureActionFragment(barcode: Barcode){

        val containerId = viewBinding.activityBarcodeInformationActionButtonFrameLayout.id

        val actionsFragment: KClass<out ActionsFragment> = get {
            parametersOf(barcode.getBarcodeType())
        }

        val args = get<Bundle>().apply {
            putSerializable(BARCODE_KEY, barcode)
        }

        replaceFragment(containerId, actionsFragment, args)
    }

    // ---- UI ----

    fun showSnackbar(text: String) {
        val snackbar = Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT)
        snackbar.anchorView = viewBinding.activityBarcodeInformationActionButtonFrameLayout
        snackbar.show()
    }

    private fun changeToolbarText(barcode: Barcode) {
        val tabText = getString(barcode.getBarcodeType().stringResource)
        supportActionBar?.title = tabText
    }

    // ---- Database Update ----

    private fun updateTypeIntoDatabase(barcodeAnalysis: BarcodeAnalysis, newBarcodeType: BarcodeType){

        val productName = when (barcodeAnalysis) {
            is BookBarcodeAnalysis -> barcodeAnalysis.title
            is FoodBarcodeAnalysis -> barcodeAnalysis.name
            else -> ""
        }

        val barcode = barcodeAnalysis.barcode

        if(!productName.isNullOrBlank()) {
            if (barcode.name != productName || barcode.getBarcodeType() != newBarcodeType)
                databaseViewModel.updateTypeAndName(barcode.scanDate, newBarcodeType, productName.trim())
        }else{
            if(barcode.getBarcodeType() != newBarcodeType)
                databaseViewModel.updateType(barcode.scanDate, newBarcodeType)
        }
        barcode.type = newBarcodeType.name
    }
}