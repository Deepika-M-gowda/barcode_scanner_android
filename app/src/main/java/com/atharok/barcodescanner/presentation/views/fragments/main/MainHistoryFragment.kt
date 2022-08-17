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

package com.atharok.barcodescanner.presentation.views.fragments.main

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.utils.BARCODE_KEY
import com.atharok.barcodescanner.common.utils.INTENT_START_ACTIVITY
import com.atharok.barcodescanner.databinding.FragmentMainHistoryBinding
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.presentation.customView.CustomItemTouchHelperCallback
import com.atharok.barcodescanner.presentation.customView.MarginItemDecoration
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.views.activities.BarcodeAnalysisActivity
import com.atharok.barcodescanner.presentation.views.activities.MainActivity
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.history.HistoryItemAdapter
import com.atharok.barcodescanner.presentation.views.recyclerView.history.HistoryItemTouchHelperListener
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

/**
 * A simple [Fragment] subclass.
 */
class MainHistoryFragment : BaseFragment(), HistoryItemAdapter.OnItemClickListener, HistoryItemTouchHelperListener {

    private val databaseViewModel: DatabaseViewModel by sharedViewModel()
    private val adapter: HistoryItemAdapter = HistoryItemAdapter(this)

    private var _binding: FragmentMainHistoryBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainHistoryBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fragmentMainHistoryRecyclerView.visibility = View.GONE
        viewBinding.fragmentMainHistoryEmptyTextView.visibility = View.GONE

        configureRecyclerView()

        databaseViewModel.barcodeList.observe(viewLifecycleOwner) {

            adapter.updateData(it)

            if (it.isEmpty()) {
                viewBinding.fragmentMainHistoryEmptyTextView.visibility = View.VISIBLE
                viewBinding.fragmentMainHistoryRecyclerView.visibility = View.GONE
            } else {
                viewBinding.fragmentMainHistoryEmptyTextView.visibility = View.GONE
                viewBinding.fragmentMainHistoryRecyclerView.visibility = View.VISIBLE
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_history, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_history_delete_all -> {showDeleteConfirmationDialog(); true}
            else -> super.onOptionsItemSelected(item)
        }
    }

    // ---- HistoryItemAdapter.OnItemClickListener Implementation ----
    override fun onItemClick(view: View?, barcode: Barcode) {
        startBarcodeAnalysisActivity(barcode)
    }

    // ---- HistoryItemTouchHelperListener Implementation ----
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        val barcode: Barcode = adapter.getItem(position)

        databaseViewModel.deleteBarcode(barcode)

        // Dans le cas de texte trop long et/ou contenant des '\n', on adapte la chaine de caractères
        val content = if (barcode.contents.length <= 16) {
            barcode.contents.substringBefore('\n')
        } else
            "${barcode.contents.substring(0, 16).substringBefore('\n')}..."

        showSnackbar(getString(R.string.snack_bar_message_item_deleted, content))
    }

    // ----

    private fun configureRecyclerView(){

        val recyclerView = viewBinding.fragmentMainHistoryRecyclerView

        //adapter = HistoryItemAdapter(this)
        val layoutManager = LinearLayoutManager(requireContext())
        val decoration = MarginItemDecoration(resources.getDimensionPixelSize(R.dimen.standard_margin))

        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(decoration)


        val itemTouchHelperCallback =
            CustomItemTouchHelperCallback(
                this,
                0,
                ItemTouchHelper.LEFT
            )
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun startBarcodeAnalysisActivity(barcode: Barcode){
        val intent = getBarcodeAnalysisActivityIntent().apply {
            putExtra(BARCODE_KEY, barcode)
        }
        startActivity(intent)
    }

    private fun getBarcodeAnalysisActivityIntent(): Intent =
        get(named(INTENT_START_ACTIVITY)) { parametersOf(BarcodeAnalysisActivity::class) }

    private fun showDeleteConfirmationDialog(){
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.popup_message_confirmation_delete_history)
            .setPositiveButton(R.string.delete_label) { _, _ ->
                databaseViewModel.deleteAll()
            }
            .setNegativeButton(R.string.cancel_label, null)
            .show()
    }

    // ---- UI ----
    private fun showSnackbar(text: String) {
        val activity = requireActivity()
        if(activity is MainActivity) {
            activity.showSnackbar(text)
        }
    }
}