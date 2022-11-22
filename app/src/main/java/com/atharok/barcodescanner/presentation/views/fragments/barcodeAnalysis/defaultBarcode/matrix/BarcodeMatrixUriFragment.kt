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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.matrix

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.INTENT_SEARCH_URL
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeMatrixUriBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.google.zxing.client.result.ParsedResult
import com.google.zxing.client.result.ParsedResultType
import com.google.zxing.client.result.URIParsedResult
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 */
class BarcodeMatrixUriFragment: AbstractBarcodeMatrixFragment() {

    private var _binding: FragmentBarcodeMatrixUriBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeMatrixUriBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    protected override fun configureMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object: MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_open_in_web_browser, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when(menuItem.itemId){
                    R.id.menu_search -> launchWebSearch()
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun start(product: BarcodeAnalysis, parsedResult: ParsedResult) {

        if(parsedResult is URIParsedResult && parsedResult.type == ParsedResultType.URI) {
            configureUri(parsedResult.uri)
            configureIsPossiblyMaliciousURI(parsedResult.isPossiblyMaliciousURI)
        }else{
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun configureUri(uri: String?) = displayText(
        textView = viewBinding.fragmentBarcodeMatrixUriUrlTextView,
        layout = viewBinding.fragmentBarcodeMatrixUriUrlLayout,
        text = uri
    )

    private fun configureIsPossiblyMaliciousURI(isPossiblyMaliciousURI: Boolean?){
        if (isPossiblyMaliciousURI != true) {
            viewBinding.fragmentBarcodeMatrixUriMaliciousLayout.visibility = View.GONE
        }
    }

    private fun launchWebSearch(): Boolean {
        arguments?.serializable(PRODUCT_KEY, BarcodeAnalysis::class.java)?.let { barcodeAnalysis ->
            if(barcodeAnalysis.barcode.getBarcodeType() == BarcodeType.URL) {
                try {
                    val intent: Intent = get(named(INTENT_SEARCH_URL)) {
                        parametersOf(barcodeAnalysis.barcode.contents)
                    }
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    showToastText(R.string.barcode_search_error_label)
                }
            }
        }

        return true
    }
}