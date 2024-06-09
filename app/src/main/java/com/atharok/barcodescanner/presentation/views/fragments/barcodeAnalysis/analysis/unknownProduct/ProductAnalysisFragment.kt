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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.analysis.unknownProduct

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
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_KEY
import com.atharok.barcodescanner.databinding.FragmentProductAnalysisBinding
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPI
import com.atharok.barcodescanner.domain.entity.analysis.RemoteAPIError
import com.atharok.barcodescanner.domain.entity.analysis.UnknownProductBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.about.BarcodeAboutOverviewFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class ProductAnalysisFragment : BarcodeAnalysisFragment<UnknownProductBarcodeAnalysis>() {

    private var product: UnknownProductBarcodeAnalysis? = null

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
                    product?.let { downloadFromRemoteAPI(it) }
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

    override fun start(analysis: UnknownProductBarcodeAnalysis) {

        this.product = analysis

        viewBinding.fragmentProductAnalysisOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureBarcodeAboutOverviewFragment()

        analysis.apiError.let { apiError ->
            when(apiError){
                RemoteAPIError.NO_INTERNET_PERMISSION, RemoteAPIError.ERROR -> {
                    configureBarcodeErrorApiFragment()
                }
                RemoteAPIError.AUTOMATIC_API_RESEARCH_DISABLED -> {
                    viewBinding.fragmentProductAnalysisSearchApiEntitledLayout.visibility = View.GONE
                    viewBinding.fragmentProductAnalysisSearchApiFrameLayout.visibility = View.GONE
                }
                RemoteAPIError.NO_RESULT -> {
                    configureBarcodeNotFoundApi(analysis)
                }
            }
        }
    }

    private fun configureBarcodeAboutOverviewFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisBarcodeAboutOverviewFrameLayout.id,
        fragmentClass = BarcodeAboutOverviewFragment::class,
        args = arguments
    )

    /**
     * Si une erreur s'est produit pendant la recherche sur les APIs
     */
    private fun configureBarcodeErrorApiFragment() = applyFragment(
        containerViewId = viewBinding.fragmentProductAnalysisSearchApiFrameLayout.id,
        fragmentClass = ProductAnalysisErrorFragment::class,
        args = arguments
    )

    /**
     * Si pas d'erreur mais pas trouvé dans les APIs distantes
     */
    private fun configureBarcodeNotFoundApi(barcodeAnalysis: BarcodeAnalysis) {
        val apiRemote = getString(barcodeAnalysis.source.nameResource)
        configureBarcodeNotFoundApiFragment(getString(R.string.barcode_not_found_on_api_label, apiRemote))
    }

    private fun configureBarcodeNotFoundApiFragment(contents: String) = configureExpandableCardViewFragment(
        frameLayout = viewBinding.fragmentProductAnalysisSearchApiFrameLayout,
        title = getString(R.string.information_label),
        contents = contents,
        iconDrawableResource = R.drawable.outline_info_24
    )

    private fun downloadFromRemoteAPI(product: UnknownProductBarcodeAnalysis) {
        requireActivity().apply {
            if(this is BarcodeAnalysisActivity) {
                val barcode = product.barcode
                when(product.apiError) {
                    RemoteAPIError.NO_RESULT -> {
                        if (!barcode.isBookBarcode()) {
                            createRemoteApiAlertDialog(barcode, this)
                        }
                    }
                    RemoteAPIError.AUTOMATIC_API_RESEARCH_DISABLED -> {
                        when(barcode.getBarcodeType()) {
                            BarcodeType.FOOD -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                            BarcodeType.BEAUTY -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                            BarcodeType.PET_FOOD -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                            BarcodeType.MUSIC -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.MUSICBRAINZ)
                            BarcodeType.BOOK -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_LIBRARY)
                            else -> createRemoteApiAlertDialog(barcode, this)
                        }
                    }
                    else -> {
                        when(barcode.getBarcodeType()) {
                            BarcodeType.FOOD -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                            BarcodeType.BEAUTY -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                            BarcodeType.PET_FOOD -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                            BarcodeType.MUSIC -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.MUSICBRAINZ)
                            BarcodeType.BOOK -> this.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_LIBRARY)
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

        val builder = MaterialAlertDialogBuilder(barcodeAnalysisActivity).apply {
            setTitle(R.string.preferences_remote_api_choose_label)
            setItems(items) { _, i ->
                when(i) {
                    0 -> barcodeAnalysisActivity.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_FOOD_FACTS)
                    1 -> barcodeAnalysisActivity.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_BEAUTY_FACTS)
                    2 -> barcodeAnalysisActivity.fetchProductFromRemoteAPI(barcode, RemoteAPI.OPEN_PET_FOOD_FACTS)
                    3 -> barcodeAnalysisActivity.fetchProductFromRemoteAPI(barcode, RemoteAPI.MUSICBRAINZ)
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
        fun newInstance(barcodeAnalysis: UnknownProductBarcodeAnalysis) =
            ProductAnalysisFragment().apply {
                arguments = get<Bundle>().apply {
                    putSerializable(BARCODE_ANALYSIS_KEY, barcodeAnalysis)
                }
            }
    }
}