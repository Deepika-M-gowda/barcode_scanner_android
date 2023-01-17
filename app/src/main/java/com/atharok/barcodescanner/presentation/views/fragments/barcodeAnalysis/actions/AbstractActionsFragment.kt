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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.ACTION_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.ACTION_SCOPE_SESSION_ID
import com.atharok.barcodescanner.databinding.FragmentBarcodeAnalysisActionsBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.presentation.intent.createSearchUrlIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionButtonAdapter
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.client.result.ParsedResult
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractActionsFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    protected val actionScope get() = getKoin().getOrCreateScope(
        ACTION_SCOPE_SESSION_ID, named(ACTION_SCOPE_SESSION)
    )

    private var _binding: FragmentBarcodeAnalysisActionsBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAnalysisActionsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        actionScope.close()
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeAnalysis) {

        val barcode: Barcode = product.barcode

        val parsedResult: ParsedResult = actionScope.get { parametersOf(barcode) }

        val actionItems = configureActions(barcode, parsedResult)

        val adapter = ActionButtonAdapter(actionItems)
        val layoutManager = GridLayoutManager(requireContext(), 3)

        val recyclerView = viewBinding.fragmentBarcodeAnalysisActionRecyclerView

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
    }

    abstract fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem>

    protected fun configureDefaultActions(contents: String) = arrayOf(
        ActionItem(R.string.action_web_search_label, R.drawable.baseline_search_24, openUrl(getSearchEngineUrl(contents))),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(contents)),
        ActionItem(R.string.copy_label, R.drawable.baseline_content_copy_24, copyContents(contents))
    )

    // ---- Actions ----

    protected fun openUrl(url: String): () -> Unit = {
        val intent = createSearchUrlIntent(url)
        startActivity(intent)
    }

    protected fun copyContents(contents: String): () -> Unit = {
        copyToClipboard("contents", contents)
        showToastText(R.string.barcode_copied_label)
    }

    protected fun shareTextContents(contents: String): () -> Unit = {
        val intent = createShareTextIntent(requireContext(), contents)
        startActivity(intent)
    }

    // ---- Utils ----

    protected fun getSearchEngineUrl(contents: String): String {
        return get<SettingsManager>().getSearchEngineUrl(contents)
    }

    protected fun showSnackbar(text: String) {
        val activity = requireActivity()
        if(activity is BarcodeAnalysisActivity) {
            activity.showSnackbar(text)
        }
    }

    protected fun mStartActivity(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            showToastText(R.string.barcode_search_error_label)//Aucune application compatible trouvé
        } catch (e: Exception) {
            showToastText(e.toString())//Url not supported
        }
    }

    protected fun createAlertDialog(context: Context, title: String, items: Array<Pair<String, () -> Unit>>): AlertDialog {
        val onClickListener = DialogInterface.OnClickListener { _, i ->
            items[i].second()
        }

        val itemsLabel: Array<String> = items.map { it.first }.toTypedArray()

        return MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
            setItems(itemsLabel, onClickListener)
        }.create()
    }
}