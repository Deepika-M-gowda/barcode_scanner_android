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
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION
import com.atharok.barcodescanner.common.utils.BARCODE_ANALYSIS_SCOPE_SESSION_ID
import com.atharok.barcodescanner.databinding.FragmentBarcodeAnalysisActionsBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.product.BarcodeAnalysis
import com.atharok.barcodescanner.domain.library.SettingsManager
import com.atharok.barcodescanner.presentation.intent.createSearchUrlIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBarcodeViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionButtonAdapter
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.zxing.client.result.ParsedResult
import org.koin.android.ext.android.get
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

abstract class AbstractActionsFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    protected val barcodeAnalysisScope get() = getKoin().getOrCreateScope(
        BARCODE_ANALYSIS_SCOPE_SESSION_ID,
        named(BARCODE_ANALYSIS_SCOPE_SESSION)
    ) // close in BarcodeAnalysisActivity

    private val databaseBarcodeViewModel: DatabaseBarcodeViewModel by activityViewModel()

    private var _binding: FragmentBarcodeAnalysisActionsBinding? = null
    private val viewBinding get() = _binding!!

    private val adapter = ActionButtonAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAnalysisActionsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: BarcodeAnalysis) {

        val barcode: Barcode = product.barcode

        val parsedResult: ParsedResult = barcodeAnalysisScope.get {
            parametersOf(barcode.contents, barcode.getBarcodeFormat())
        }

        val actionItems = configureActions(barcode, parsedResult)

        configureRecyclerView(actionItems)

        configureDatabaseObserver(barcode, parsedResult)
    }

    /**
     * On ajoute le bouton DeleteActionItem si le Barcode est présent dans la BDD. Sinon on ne
     * l'affiche pas.
     * Si le Barcode a été supprimé de la BDD via le bouton DeleteActionItem, on met à jour
     * automatiquement l'adapter permettant de ne plus afficher le bouton.
     */
    private fun configureDatabaseObserver(barcode: Barcode, parsedResult: ParsedResult) {
        databaseBarcodeViewModel.getBarcodeByDate(barcode.scanDate).observe(viewLifecycleOwner) {
            val items = if(it!=null){
                configureActions(barcode, parsedResult) + configureDeleteBarcodeFromHistoryActionItem(barcode)
            } else {
                configureActions(barcode, parsedResult) + configureAddBarcodeInHistoryActionItem(barcode)
            }
            adapter.updateData(items)
        }
    }

    private fun configureRecyclerView(actionItems: Array<ActionItem>) {

        val layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.grid_layout_span_count))

        val recyclerView = viewBinding.fragmentBarcodeAnalysisActionRecyclerView

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        adapter.updateData(actionItems)
    }

    abstract fun configureActions(barcode: Barcode, parsedResult: ParsedResult): Array<ActionItem>

    protected fun configureDefaultActions(barcode: Barcode) = arrayOf(
        ActionItem(R.string.action_web_search_label, R.drawable.baseline_search_24, openContentsWithSearchEngine(barcode.contents)),
        ActionItem(R.string.share_text_label, R.drawable.baseline_share_24, shareTextContents(barcode.contents)),
        ActionItem(R.string.copy_label, R.drawable.baseline_content_copy_24, copyContents(barcode.contents))
    )

    private fun configureDeleteBarcodeFromHistoryActionItem(barcode: Barcode): ActionItem {
        return ActionItem(R.string.menu_item_history_delete_from_history, R.drawable.baseline_delete_forever_24, deleteBarcodeFromHistory(barcode))
    }

    private fun configureAddBarcodeInHistoryActionItem(barcode: Barcode): ActionItem {
        return ActionItem(R.string.menu_item_history_add_in_history, R.drawable.baseline_add_24, addBarcodeInHistory(barcode))
    }

    // ---- Actions ----

    protected fun openUrl(url: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createSearchUrlIntent(url)
            mStartActivity(intent)
        }
    }

    /**
     * Ouvre le contenu du code-barres avec le moteur de recherche définie dans les paramètres.
     */
    protected fun openContentsWithSearchEngine(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = get<SettingsManager>().getSearchEngineIntent(contents)
            mStartActivity(intent)
        }
    }

    protected fun copyContents(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            copyToClipboard("contents", contents)
            showToastText(R.string.barcode_copied_label)
        }
    }

    protected fun shareTextContents(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createShareTextIntent(requireContext(), contents)
            startActivity(intent)
        }
    }

    private fun deleteBarcodeFromHistory(barcode: Barcode): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            databaseBarcodeViewModel.deleteBarcode(barcode)
            showSnackbar(getString(R.string.menu_item_history_removed_from_history))
        }
    }

    private fun addBarcodeInHistory(barcode: Barcode): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            databaseBarcodeViewModel.insertBarcode(barcode)
            showSnackbar(getString(R.string.menu_item_history_added_in_history))
        }
    }

    // ---- Utils ----

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

    protected fun createAlertDialog(context: Context, title: String, items: Array<Pair<String, ActionItem.OnActionItemListener>>): AlertDialog {
        val onClickListener = DialogInterface.OnClickListener { _, i ->
            items[i].second.onItemClick(null)
        }

        val itemsLabel: Array<String> = items.map { it.first }.toTypedArray()

        return AlertDialog.Builder(context).apply {
            setTitle(title)
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
            setItems(itemsLabel, onClickListener)
        }.create()
    }
}