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
import com.atharok.barcodescanner.databinding.FragmentBarcodeAnalysisActionsBinding
import com.atharok.barcodescanner.domain.entity.analysis.BarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.presentation.intent.createSearchUrlIntent
import com.atharok.barcodescanner.presentation.intent.createShareTextIntent
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBarcodeViewModel
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseCustomUrlViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionButtonAdapter
import com.atharok.barcodescanner.presentation.views.recyclerView.actionButton.ActionItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.core.parameter.parametersOf

abstract class AbstractActionsFragment : BarcodeAnalysisFragment<BarcodeAnalysis>() {

    private val databaseBarcodeViewModel: DatabaseBarcodeViewModel by activityViewModel()
    private val databaseCustomUrlViewModel: DatabaseCustomUrlViewModel by activityViewModel()

    private var _binding: FragmentBarcodeAnalysisActionsBinding? = null
    private val viewBinding get() = _binding!!

    private var alertDialog: AlertDialog? = null
    private val adapter = ActionButtonAdapter()
    private val actionItems = mutableListOf<ActionItem>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeAnalysisActionsBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onDestroy() {
        super.onDestroy()
        alertDialog?.dismiss()
    }

    override fun start(analysis: BarcodeAnalysis) {
        actionItems.clear()
        val barcode = analysis.barcode
        configureActionItems(barcode)
        configureRecyclerView()
        configureCustomUrlDatabaseObserver(barcode)
        configureBarcodeDatabaseObserver(barcode)
    }

    private fun configureRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), resources.getInteger(R.integer.grid_layout_span_count))
        val recyclerView = viewBinding.fragmentBarcodeAnalysisActionRecyclerView

        recyclerView.isNestedScrollingEnabled = false
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        adapter.updateData(actionItems)
    }

    abstract fun configureActionItems(barcode: Barcode)

    protected fun addActionItem(actionItem: ActionItem): Boolean = actionItems.add(actionItem)
    protected fun addActionItem(index: Int, actionItem: ActionItem) = actionItems.add(index, actionItem)
    private fun removeActionItem(actionItem: ActionItem) = actionItems.remove(actionItem)

    // ---- Database observers ----

    private fun configureCustomUrlDatabaseObserver(barcode: Barcode) {
        databaseCustomUrlViewModel.customUrlList.observe(viewLifecycleOwner) { urls ->
            if(urls.isNotEmpty()) {
                val items: Array<Pair<String, ActionItem.OnActionItemListener>> = urls.map {
                    getString(R.string.action_product_search_label, it.name) to openUrl(it.getUrlWithContents(barcode.contents))
                }.toTypedArray()

                addActionItem(configureCustomUrlActionItem(items))

                adapter.updateData(actionItems)
            }
        }
    }

    private var deleteBarcodeFromHistoryActionItem: ActionItem? = null
    private var addBarcodeInHistoryActionItem: ActionItem? = null

    /**
     * On ajoute le bouton DeleteActionItem si le Barcode est présent dans la BDD. Sinon on ne
     * l'affiche pas.
     * Si le Barcode a été supprimé de la BDD via le bouton DeleteActionItem, on met à jour
     * automatiquement l'adapter permettant de ne plus afficher le bouton.
     */
    private fun configureBarcodeDatabaseObserver(barcode: Barcode) {
        databaseBarcodeViewModel.getBarcodeByDate(barcode.scanDate).observe(viewLifecycleOwner) {
            addBarcodeInHistoryActionItem?.let { actionItem -> removeActionItem(actionItem) }
            deleteBarcodeFromHistoryActionItem?.let { actionItem -> removeActionItem(actionItem) }

            if(it!=null) {
                if(deleteBarcodeFromHistoryActionItem == null)
                    deleteBarcodeFromHistoryActionItem = configureDeleteBarcodeFromHistoryActionItem(barcode)
                deleteBarcodeFromHistoryActionItem?.let { actionItem -> addActionItem(actionItem) }
            } else {
                if(addBarcodeInHistoryActionItem == null)
                    addBarcodeInHistoryActionItem = configureAddBarcodeInHistoryActionItem(barcode)
                addBarcodeInHistoryActionItem?.let { actionItem -> addActionItem(actionItem) }
            }

            adapter.updateData(actionItems)
        }
    }

    // ---- ActionItem Configuration ----

    // Search on the Web
    protected fun configureSearchOnWebActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.action_web_search_label,
            imageRes = R.drawable.baseline_search_24,
            listener = openContentsWithSearchEngine(barcode.contents)
        )
    }

    // Share text
    protected fun configureShareTextActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.share_text_label,
            imageRes = R.drawable.baseline_share_24,
            listener = shareTextContents(barcode.contents)
        )
    }

    // Copy text
    protected fun configureCopyTextActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.copy_barcode_label,
            imageRes = R.drawable.baseline_content_copy_24,
            listener = copyContents(barcode.contents)
        )
    }

    // Modify barcode
    protected fun configureModifyBarcodeActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.action_modify_barcode,
            imageRes = R.drawable.baseline_create_24,
            listener = modifyBarcodeContents(barcode)
        )
    }

    // Delete from history
    private fun configureDeleteBarcodeFromHistoryActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.menu_item_history_delete_from_history,
            imageRes = R.drawable.baseline_delete_forever_24,
            listener = deleteBarcodeFromHistory(barcode)
        )
    }

    // Add to history
    private fun configureAddBarcodeInHistoryActionItem(barcode: Barcode): ActionItem {
        return ActionItem(
            textRes = R.string.menu_item_history_add_in_history,
            imageRes = R.drawable.baseline_add_24,
            listener = addBarcodeInHistory(barcode)
        )
    }

    // Search on custom URL
    private fun configureCustomUrlActionItem(items: Array<Pair<String, ActionItem.OnActionItemListener>>): ActionItem {
        return ActionItem(
            textRes = R.string.custom_urls,
            imageRes = R.drawable.baseline_search_24,
            listener = object : ActionItem.OnActionItemListener {
                override fun onItemClick(view: View?) {
                    createAlertDialog(requireContext(), getString(R.string.custom_urls), items).show()
                }
            }
        )
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
            val intent = settingsManager.getSearchEngineIntent(contents)
            mStartActivity(intent)
        }
    }

    private fun copyContents(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            copyToClipboard("contents", contents)
            showToastText(R.string.barcode_copied_label)
        }
    }

    private fun shareTextContents(contents: String): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val intent = createShareTextIntent(requireContext(), contents)
            startActivity(intent)
        }
    }

    private fun modifyBarcodeContents(barcode: Barcode): ActionItem.OnActionItemListener = object : ActionItem.OnActionItemListener {
        override fun onItemClick(view: View?) {
            val bottomSheetFragment:
                    BarcodeContentsModifierModalBottomSheetFragment = get { parametersOf(barcode) }
            bottomSheetFragment.show(requireActivity().supportFragmentManager, "BarcodeContentsModifierModalBottomSheetFragment")
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
            databaseBarcodeViewModel.insertBarcode(barcode, true)
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
            showToastText(R.string.barcode_search_error_no_compatible_application_found)
        } catch (e: Exception) {
            showToastText("${getString(R.string.barcode_search_error_label)} : $e")
        }
    }

    protected fun createAlertDialog(context: Context, title: String, items: Array<Pair<String, ActionItem.OnActionItemListener>>): AlertDialog {
        val onClickListener = DialogInterface.OnClickListener { _, i ->
            items[i].second.onItemClick(null)
        }

        val itemsLabel: Array<String> = items.map { it.first }.toTypedArray()

        alertDialog = MaterialAlertDialogBuilder(context).apply {
            setTitle(title)
            setNegativeButton(R.string.close_dialog_label) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            setItems(itemsLabel, onClickListener)
        }.create()
        return alertDialog!!
    }
}