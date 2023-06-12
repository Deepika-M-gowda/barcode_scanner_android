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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.API_ERROR_KEY
import com.atharok.barcodescanner.common.utils.BARCODE_MESSAGE_ERROR_KEY
import com.atharok.barcodescanner.common.utils.IGNORE_USE_SEARCH_ON_API_SETTING_KEY
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentProductAnalysisBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.DefaultBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.RemoteAPI
import com.atharok.barcodescanner.domain.entity.product.RemoteAPIError
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeAnalysisErrorApiFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root.BarcodeAnalysisInformationFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class ProductAnalysisFragment : BarcodeAnalysisFragment<DefaultBarcodeAnalysis>() {

    private var barcode: Barcode? = null

    private var _binding: FragmentProductAnalysisBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProductAnalysisBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun configureMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_activity_barcode_analysis, menu)

                // On retire les menus inutile
                menu.removeItem(R.id.menu_activity_barcode_analysis_product_source_api_info_item)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when(menuItem.itemId){

                R.id.menu_activity_barcode_analysis_download_from_apis -> {
                    barcode?.let { downloadFromRemoteAPI(it) }
                    true
                }

                R.id.menu_activity_barcode_analysis_about_barcode_item -> {
                    startBarcodeDetailsActivity()
                    true
                }
                else -> false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun start(product: DefaultBarcodeAnalysis) {

        this.barcode = product.barcode

        viewBinding.fragmentProductAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureAboutBarcodeFragment()

        arguments?.serializable(API_ERROR_KEY, RemoteAPIError::class.java)?.let { apiError ->
            when(apiError){
                RemoteAPIError.NO_INTERNET_PERMISSION, RemoteAPIError.ERROR -> {
                    configureProductSearchApiEntitledLayout()
                    configureBarcodeErrorApiFragment()
                }
                RemoteAPIError.NO_API_RESEARCH -> {
                    viewBinding.fragmentProductAnalysisSearchApiEntitledLayout.visibility = View.GONE
                    viewBinding.fragmentProductAnalysisSearchApiFrameLayout.visibility = View.GONE
                }
                RemoteAPIError.NO_RESULT -> {
                    configureProductSearchApiEntitledLayout()
                    configureBarcodeNotFoundApi(product)
                }
            }
        }
    }

    private fun configureProductSearchApiEntitledLayout(){
        val entitled: String = getString(R.string.product_search_label)
        viewBinding.fragmentProductAnalysisProductSearchApiEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisAboutBarcodeFrameLayout.id,
        fragmentClass = BarcodeAnalysisInformationFragment::class,
        args = arguments
    )

    /**
     * Si une erreur s'est produit pendant la recherche sur les APIs
     */
    private fun configureBarcodeErrorApiFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisSearchApiFrameLayout.id,
        fragmentClass = BarcodeAnalysisErrorApiFragment::class,
        args = arguments
    )

    /**
     * Si pas d'erreur mais pas trouvé dans les APIs distantes
     */
    private fun configureBarcodeNotFoundApi(barcodeAnalysis: BarcodeAnalysis) {
        val apiRemote = getString(barcodeAnalysis.source.nameResource)
        configureBarcodeNotFoundApiFragment(getString(R.string.barcode_not_found_on_api_label, apiRemote))
    }

    private fun configureBarcodeNotFoundApiFragment(contents: String) = configureExpandableViewFragment(
        frameLayout = viewBinding.fragmentProductAnalysisSearchApiFrameLayout,
        title = getString(R.string.information_label),
        contents = contents,
        iconDrawableResource = R.drawable.outline_info_24
    )

    private fun downloadFromRemoteAPI(barcode: Barcode) {
        requireActivity().apply {
            if(this is BarcodeAnalysisActivity) {
                val apiError: RemoteAPIError? = arguments?.serializable(API_ERROR_KEY, RemoteAPIError::class.java)
                when(apiError) {
                    RemoteAPIError.NO_RESULT -> {
                        if (!barcode.isBookBarcode()) {
                            createRemoteApiAlertDialog(barcode, this)
                        }
                    }
                    RemoteAPIError.NO_API_RESEARCH -> {
                        intent.putExtra(IGNORE_USE_SEARCH_ON_API_SETTING_KEY, true)
                        when(barcode.getBarcodeType()) {
                            BarcodeType.FOOD -> this.restartApiResearch(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                            BarcodeType.BEAUTY -> this.restartApiResearch(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                            BarcodeType.PET_FOOD -> this.restartApiResearch(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                            BarcodeType.MUSIC -> this.restartApiResearch(barcode, RemoteAPI.MUSICBRAINZ)
                            BarcodeType.BOOK -> this.restartApiResearch(barcode, RemoteAPI.OPEN_LIBRARY)
                            else -> createRemoteApiAlertDialog(barcode, this)
                        }
                    }
                    else -> {
                        when(barcode.getBarcodeType()) {
                            BarcodeType.FOOD -> this.restartApiResearch(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                            BarcodeType.BEAUTY -> this.restartApiResearch(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                            BarcodeType.PET_FOOD -> this.restartApiResearch(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                            BarcodeType.MUSIC -> this.restartApiResearch(barcode, RemoteAPI.MUSICBRAINZ)
                            BarcodeType.BOOK -> this.restartApiResearch(barcode, RemoteAPI.OPEN_LIBRARY)
                            else -> createRemoteApiAlertDialog(barcode, this)
                        }
                    }
                }
            }
        }
    }

    private fun createRemoteApiAlertDialog(barcode: Barcode, barcodeAnalysisActivity: BarcodeAnalysisActivity) {
        val items = arrayOf(
            getString(R.string.preferences_remote_api_food_label),
            getString(R.string.preferences_remote_api_cosmetic_label),
            getString(R.string.preferences_remote_api_pet_food_label),
            getString(R.string.preferences_remote_api_musicbrainz_label)
        )

        val builder = MaterialAlertDialogBuilder(barcodeAnalysisActivity, R.style.AppTheme_MaterialAlertDialog).apply {
            setTitle(R.string.preferences_remote_api_choose_label)
            setItems(items) { dialog, i ->
                when(i) {
                    0 -> barcodeAnalysisActivity.restartApiResearch(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                    1 -> barcodeAnalysisActivity.restartApiResearch(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                    2 -> barcodeAnalysisActivity.restartApiResearch(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                    3 -> barcodeAnalysisActivity.restartApiResearch(barcode, RemoteAPI.MUSICBRAINZ)
                }
            }
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
        }

        val dialog = builder.create()
        dialog.show()
    }

    companion object {
        fun newInstance(defaultBarcodeAnalysis: DefaultBarcodeAnalysis, apiError: RemoteAPIError, errorMessage: String?) = ProductAnalysisFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, defaultBarcodeAnalysis)
                putSerializable(API_ERROR_KEY, apiError)
                errorMessage?.let {
                    putString(BARCODE_MESSAGE_ERROR_KEY, it)
                }
            }
        }
    }
}