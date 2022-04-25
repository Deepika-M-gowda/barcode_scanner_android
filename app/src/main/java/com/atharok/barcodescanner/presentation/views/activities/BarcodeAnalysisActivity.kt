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
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.databinding.ActivityBarcodeAnalysisBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.BarcodeProduct
import com.atharok.barcodescanner.domain.entity.product.BookProduct
import com.atharok.barcodescanner.domain.entity.product.NoneProduct
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ProductViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root.BarcodeDefaultFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.bookProduct.BookProductFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.ingredients.FoodProductRootIngredientsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.nutritionFacts.FoodProductRootNutritionFactsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.overview.FoodProductRootOverviewFragment
import com.atharok.barcodescanner.presentation.views.viewPagerAdapters.BarcodeAnalysisPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
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

    private var sourceApiInfoAlertDialog: AlertDialog? = null

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

        // Permet de pré-charger les deux Fragments supplémentaires du ViewPager
        viewBinding.activityBarcodeInformationViewPager.offscreenPageLimit = 2

        setSupportActionBar(viewBinding.activityBarcodeInformationToolbar.toolbar)

        val barcode: Barcode? = intent?.getSerializableExtra(BARCODE_KEY) as Barcode?

        if(barcode!=null){
            configureAllViews(barcode)
        } else {
            //showSnackbar(getString(R.string.scan_error_label))
            viewBinding.activityBarcodeInformationProgressBar.visibility = View.GONE
        }

        setContentView(viewBinding.root)
    }

    override fun onDestroy() {
        barcodeAnalysisScope.close()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_barcode_analysis, menu)

        if(sourceApiInfoAlertDialog == null)
            disableSourceApiInfoOptionMenu()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_activity_barcode_analysis_product_source_api_info_item -> {
                sourceApiInfoAlertDialog?.show()
                true
            }
            R.id.menu_activity_barcode_analysis_about_barcode_item -> {
                startBarcodeImageActivity()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureAllViews(barcode: Barcode){
        //configureActionFragment(barcode.scanDate) // Actions (FAB)
        configureContentsView(barcode)
    }

    private fun configureContentsView(barcode: Barcode){

        changeToolbarText(barcode)
        disableSourceApiInfoOptionMenu()

        when {
            barcode.is1DProductBarcodeFormat -> configureApiResearch(barcode)
            barcode.is1DIndustrialBarcodeFormat -> configureDefaultView(NoneProduct(barcode), BarcodeType.INDUSTRIAL)
            barcode.is2DBarcodeFormat -> configureMatrixCodeView(NoneProduct(barcode))
            else -> {
                configureDefaultView(NoneProduct(barcode), BarcodeType.UNKNOWN)
            }
        }
    }

    private fun configureApiResearch(barcode: Barcode){

        val type = if(barcode.isBookBarcode()) BarcodeType.BOOK else BarcodeType.UNKNOWN_PRODUCT

        when {
            !checkInternetPermission() -> configureDefaultView(NoneProduct(barcode), type, getString(R.string.no_internet_permission))
            settingsManager.useSearchOnApiKey -> observeOnAPI(barcode, type)
            else -> configureDefaultView(NoneProduct(barcode), type)
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

                is Resource.Failure -> configureDefaultView(
                    NoneProduct(barcode),
                    defaultBarcodeType,
                    it.throwable.localizedMessage
                )//configureErrorSearchingProductView(barcodeInformation)

                is Resource.Success -> {
                    when (it.data) {
                        is BookProduct, is FoodProduct -> configureProductView(it.data)
                        is NoneProduct -> configureDefaultView(it.data, defaultBarcodeType)
                        else -> configureDefaultView(NoneProduct(barcode), defaultBarcodeType)
                    }
                }

                // Si le code-barres n'a été trouvé sur aucun des services distants
                else -> configureDefaultView(NoneProduct(barcode), defaultBarcodeType)
            }
        }
    }

    // ---- Configuration de la vue principale en fonction du type de code-barres / produits ----

    private fun configureProductView(barcodeProduct: BarcodeProduct){

        val productName = when (barcodeProduct) {
            is BookProduct -> barcodeProduct.title
            is FoodProduct -> barcodeProduct.name
            else -> ""
        }

        // Configure le menu permettant d'fficher à l'utilisateur la source des données
        enableSourceApiInfoOptionMenu(
            titleResource = barcodeProduct.source.nameResource,
            layout = barcodeProduct.source.layout,
            urlResource = barcodeProduct.source.urlResource
        )

        // On met à jour le type de produit dans la base de données
        updateTypeAndNameIntoDatabase(
            barcode = barcodeProduct.barcode,
            newBarcodeType = barcodeProduct.source.barcodeType,
            newName = productName
        )

        // Créer la vue
        when (barcodeProduct) {
            is BookProduct -> configureBookProductView(barcodeProduct)
            is FoodProduct -> configureFoodProductView(barcodeProduct)
        }

        // Message d'information
        /*val msg = getString(
            R.string.barcode_found_on_label,
            getString(barcodeProduct.source.nameResource)
        )
        showSnackbar(msg)*/

        // Change le texte de la toolbar
        changeToolbarText(barcodeProduct.barcode)
    }
    
    private fun configureBookProductView(bookProduct: BookProduct){

        val bookProductFragment = BookProductFragment.newInstance(bookProduct)
        val adapter = BarcodeAnalysisPagerAdapter(supportFragmentManager, lifecycle, bookProductFragment)

        configureViewPager(adapter, getString(R.string.overview_tab_label))
        configureActionFragment(bookProduct.barcode)
    }

    private fun configureFoodProductView(foodProduct: FoodProduct){

        val overviewFragment = FoodProductRootOverviewFragment.newInstance(foodProduct)
        val ingredientsFragment = FoodProductRootIngredientsFragment.newInstance(foodProduct)
        val nutritionFragment = FoodProductRootNutritionFactsFragment.newInstance(foodProduct)
        val adapter = BarcodeAnalysisPagerAdapter(supportFragmentManager, lifecycle, overviewFragment, ingredientsFragment, nutritionFragment)

        val overview: String = getString(R.string.overview_tab_label)
        val ingredients: String = getString(R.string.ingredients_label)
        val nutrition: String = getString(R.string.nutrition_facts_tab_label)
        configureViewPager(adapter, overview, ingredients, nutrition)
        configureActionFragment(foodProduct.barcode)
    }

    private fun configureDefaultView(noneProduct: NoneProduct, barcodeType: BarcodeType, messageError: String? = null){
        disableSourceApiInfoOptionMenu()

        // Si on a un message d'erreur ET Si la connexion internet est désactivé
        /*if(messageError != null && !isInternetAvailable())
            registerNetworkCallback()*/

        updateTypeIntoDatabase(barcode = noneProduct.barcode, newBarcodeType = barcodeType)

        val defaultProductFragment = BarcodeDefaultFragment.newInstance(noneProduct, messageError)
        val adapter = BarcodeAnalysisPagerAdapter(supportFragmentManager, lifecycle, defaultProductFragment)

        configureViewPager(adapter, getString(R.string.information_label))
        configureActionFragment(noneProduct.barcode)

        /*val format = noneProduct.barcode.formatName.replace("_", " ")
        val msg = getString(R.string.barcode_scanned_label, format)
        showSnackbar(msg)*/
        changeToolbarText(noneProduct.barcode)
    }

    private fun configureMatrixCodeView(noneProduct: NoneProduct){

        val barcodeType = barcodeAnalysisScope.get<BarcodeType> {
            parametersOf(noneProduct.barcode.contents, noneProduct.barcode.getBarcodeFormat())
        }

        configureDefaultView(noneProduct, barcodeType)
    }

    /*private fun registerNetworkCallback(){
        val cm: ConnectivityManager = get()
        cm.registerNetworkCallback(NetworkRequest.Builder().build(), networkCallback)
    }

    private fun unregisterNetworkCallback(){
        val cm: ConnectivityManager = get()
        cm.unregisterNetworkCallback(networkCallback)
    }

    private val networkCallback: NetworkCallback = object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            runOnUiThread {
                //configureContentsView(barcodeInformation)
                //retrofitViewModel.refresh(barcode)
                //recreate()
                retrofitViewModel.refresh()
            }
        }
    }*/

    // ---- ViewPager Configuration ----
    private fun configureViewPager(adapter: FragmentStateAdapter, vararg textTab: String){

        viewBinding.activityBarcodeInformationViewPager.adapter=adapter

        if(textTab.isNotEmpty()) {
            TabLayoutMediator(
                viewBinding.activityBarcodeInformationTabLayout,
                viewBinding.activityBarcodeInformationViewPager) { tab, position ->
                tab.text = textTab[position]
            }.attach()

            viewBinding.activityBarcodeInformationTabLayout.visibility = View.VISIBLE
        }else{
            viewBinding.activityBarcodeInformationTabLayout.visibility = View.GONE
        }

        viewBinding.activityBarcodeInformationProgressBar.visibility = View.GONE
    }

    // ---- Action Fragment ----
    /*private fun configureActionFragment(dateTimestamp: Long){

        databaseViewModel.getBarcodeByDate(dateTimestamp).observe(this) { barcode ->

            val containerId = viewBinding.activityBarcodeInformationActionButtonFrameLayout.id

            val actionsFragment: KClass<out ActionsFragment> = get {
                parametersOf(barcode.getBarcodeType())
            }

            val args = get<Bundle>().apply {
                putSerializable(BARCODE_KEY, barcode)
            }

            replaceFragment(containerId, actionsFragment, args)
        }
    }*/

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

    // ---- Activity ----

    private fun startBarcodeImageActivity(){

        val barcode: Barcode? = intent?.getSerializableExtra(BARCODE_KEY) as Barcode?

        if(barcode != null) {
            val intent = getStartBarcodeDetailsActivityIntent().apply {
                putExtra(BARCODE_CONTENTS_KEY, barcode.contents)
                putExtra(BARCODE_FORMAT_KEY, barcode.formatName)
            }

            startActivity(intent)
        }
    }

    private fun getStartBarcodeDetailsActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(BarcodeDetailsActivity::class) }

    // ---- UI ----
    /*private fun showSnackbar(text: String) {
        Snackbar.make(viewBinding.root, text, Snackbar.LENGTH_SHORT).show()
    }*/

    private fun changeToolbarText(barcode: Barcode) {
        val tabText = getString(barcode.getBarcodeType().stringResource)
        supportActionBar?.title = tabText
    }

    private fun enableSourceApiInfoOptionMenu(titleResource: Int, layout: Int, urlResource: Int){
        viewBinding.activityBarcodeInformationToolbar.toolbar.menu?.findItem(R.id.menu_activity_barcode_analysis_product_source_api_info_item)?.isVisible = true
        sourceApiInfoAlertDialog = AlertDialog.Builder(this).apply {
            setTitle(getString(titleResource))
            setView(layout)
            setNegativeButton(R.string.close_dialog_label) { dialogInterface, _ -> dialogInterface.cancel()
            }
            setPositiveButton(R.string.go_to_dialog_label) { _, _ ->
                val intent: Intent = get(named(INTENT_SEARCH_URL)) { parametersOf(getString(urlResource)) }
                startActivity(intent)
            }
        }.create()
    }

    private fun disableSourceApiInfoOptionMenu(){
        viewBinding.activityBarcodeInformationToolbar.toolbar.menu?.findItem(R.id.menu_activity_barcode_analysis_product_source_api_info_item)?.isVisible = false
        sourceApiInfoAlertDialog = null
    }

    // ---- Database Update ----

    private fun updateTypeIntoDatabase(barcode: Barcode, newBarcodeType: BarcodeType){
        if(barcode.getBarcodeType() != newBarcodeType)
            databaseViewModel.updateType(barcode.scanDate, newBarcodeType)

        barcode.type = newBarcodeType.name
    }

    /**
     * Pour un produit, si on l'a trouvé via les APIs, on ajoute son nom dans la base de données en plus de mettre à jour le type.
     */
    private fun updateTypeAndNameIntoDatabase(barcode: Barcode, newBarcodeType: BarcodeType, newName: String?){
        if(!newName.isNullOrBlank()) {
            if (barcode.name != newName || barcode.getBarcodeType() != newBarcodeType)
                databaseViewModel.updateTypeAndName(barcode.scanDate, newBarcodeType, newName.trim())
            barcode.type = newBarcodeType.name
        }else{
            updateTypeIntoDatabase(barcode, newBarcodeType) // -> Met juste le type de barcode à jour dans la BDD
        }
    }
}