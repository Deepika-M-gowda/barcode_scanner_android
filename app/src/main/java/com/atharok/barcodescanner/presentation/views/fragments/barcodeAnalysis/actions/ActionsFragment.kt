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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.databinding.FragmentBarcodeActionsBinding
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.google.zxing.client.result.ParsedResult
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class ActionsFragment: BaseFragment() {

    protected val myScope get() = getKoin().getOrCreateScope(
        ACTION_SCOPE_SESSION_ID, named(ACTION_SCOPE_SESSION)
    )

    /**
     * Le scope crée dans le BarcodeAnalysisActivity
     */
    private val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    )

    private var _binding: FragmentBarcodeActionsBinding? = null
    protected val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBarcodeActionsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        myScope.close()
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf {
            it.containsKey(BARCODE_KEY)
        }?.let {
            viewBinding.fragmentBarcodeActionsFloatingActionMenu.removeAllItems()

            it.serializable(BARCODE_KEY, Barcode::class.java)?.let { barcode ->
                addCopyActionFAB(barcode.contents)
                addShareActionFAB(barcode.contents)

                val parsedResult = barcodeAnalysisScope.get<ParsedResult> {
                    parametersOf(barcode.contents, barcode.getBarcodeFormat())
                }

                start(barcode, parsedResult)
            }
        }
    }

    private fun addCopyActionFAB(contents: String){
        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(ActionEnum.COPY_TEXT.drawableResource) {
            copyToClipboard("contents", contents)
            showToastText(R.string.barcode_copied_label)
        }
    }

    private fun addShareActionFAB(contents: String){
        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(ActionEnum.SHARE_TEXT.drawableResource) {
            val intent: Intent = get(named(INTENT_SHARE_TEXT)) { parametersOf(contents) }
            startActivity(intent)
        }
    }

    /**
     * Pas utile dans tous les cas, donc doit être appelé manuellement dans les sous classes où c'est nécessaire.
     * Par exemple pour les produits, on propose de chercher via différent liens donc on passe par un Dialog listant tous les liens.
     */
    protected fun addSearchWithEngineActionFAB(contents: String){

        val action = ActionEnum.SEARCH_WITH_ENGINE
        val url = get<SettingsManager>().getSearchEngineUrl(contents)

        viewBinding.fragmentBarcodeActionsFloatingActionMenu.addItem(action.drawableResource) {
            val intent = myScope.get<Intent>(named(action)) { parametersOf(url) }
            startActivity(intent)
        }
    }

    abstract fun start(barcode: Barcode, parsedResult: ParsedResult)
}