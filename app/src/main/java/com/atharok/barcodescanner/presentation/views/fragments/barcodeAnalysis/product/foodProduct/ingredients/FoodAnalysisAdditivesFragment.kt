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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisAdditivesBinding
import com.atharok.barcodescanner.databinding.TemplateRecyclerViewBinding
import com.atharok.barcodescanner.databinding.TemplateTextViewTitleBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.presentation.viewmodel.ExternalFileViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.presentation.views.recyclerView.additives.AdditivesItemAdapter
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisAdditivesFragment: BarcodeAnalysisFragment<FoodBarcodeAnalysis>() {

    private val viewModel: ExternalFileViewModel by sharedViewModel()
    private var additivesAdapter: AdditivesItemAdapter? = null

    private var _binding: FragmentFoodAnalysisAdditivesBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var additivesHeaderTextViewTemplateBinding: TemplateTextViewTitleBinding
    private lateinit var additivesBodyRecyclerViewTemplateBinding: TemplateRecyclerViewBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding=FragmentFoodAnalysisAdditivesBinding.inflate(inflater, container, false)
        configureIngredientsTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    // ---- Templates Configuration ----

    private fun configureIngredientsTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.additivesExpandableViewTemplate

        expandableViewTemplate.root.open() // L'ExpandableView est ouvert par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody =  expandableViewTemplate.templateExpandableViewBodyFrameLayout

        additivesHeaderTextViewTemplateBinding = TemplateTextViewTitleBinding.inflate(inflater, parentHeader, true)
        additivesBodyRecyclerViewTemplateBinding = TemplateRecyclerViewBinding.inflate(inflater, parentBody, true)
    }

    override fun start(product: FoodBarcodeAnalysis) {

        val additivesTagsList = product.additivesTagsList

        if(!additivesTagsList.isNullOrEmpty()) {
            configureEntitledView()
            configureRecyclerView()
            observeAdditives(additivesTagsList)
        } else {
            viewBinding.root.visibility = View.GONE
        }
    }

    private fun observeAdditives(additivesTagsList: List<String>) {

        viewModel.obtainAdditivesList(additivesTagsList).observe(viewLifecycleOwner) {
            additivesAdapter?.update(it)
            viewBinding.additivesProgressBar.visibility = View.GONE
            viewBinding.additivesCardView.visibility = View.VISIBLE
        }
    }

    private fun configureEntitledView() {
        additivesHeaderTextViewTemplateBinding.root.text = getString(R.string.additives_label)
    }

    private fun configureRecyclerView(){
        val linearLayoutManager = LinearLayoutManager(requireContext())
        val dividerItemDecoration = DividerItemDecoration(requireContext(), linearLayoutManager.orientation)
        additivesAdapter = AdditivesItemAdapter(requireActivity())

        val additivesRecyclerView = additivesBodyRecyclerViewTemplateBinding.root

        additivesRecyclerView.adapter = additivesAdapter
        additivesRecyclerView.layoutManager = linearLayoutManager
        additivesRecyclerView.addItemDecoration(dividerItemDecoration)
        additivesRecyclerView.suppressLayout(true)
    }
}
