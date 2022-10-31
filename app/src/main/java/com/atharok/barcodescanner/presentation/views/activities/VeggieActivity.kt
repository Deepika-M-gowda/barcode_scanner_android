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

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.ActivityVeggieBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.foodProduct.VeggieIngredientAnalysis
import com.atharok.barcodescanner.presentation.views.recyclerView.veggie.VeggieItemAdapter
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY

class VeggieActivity : BaseActivity() {

    private val viewBinding: ActivityVeggieBinding by lazy { ActivityVeggieBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        configureToolbar()

        val foodProduct: FoodBarcodeAnalysis? = intent.getSerializableExtra(PRODUCT_KEY, FoodBarcodeAnalysis::class.java)

        val veggieIngredientsList = foodProduct?.veggieIngredientList
        if(veggieIngredientsList.isNullOrEmpty()) {
            viewBinding.activityVeggieIngredientsListLayout.visibility = View.GONE
            viewBinding.activityVeggieNoIngredientsTextView.visibility = View.VISIBLE
        }else{
            configureEntitled()
            configureRecyclerView(veggieIngredientsList)
            viewBinding.activityVeggieIngredientsListLayout.visibility = View.VISIBLE
            viewBinding.activityVeggieNoIngredientsTextView.visibility = View.GONE
        }

        setContentView(viewBinding.root)
    }

    private fun configureToolbar(){
        setSupportActionBar(viewBinding.activityVeggieToolbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)// On affiche l'icone "retour"
    }

    private fun configureEntitled(){
        val entitled: String = getString(R.string.ingredient_veggie_entitled_label)
        viewBinding.activityVeggieIngredientsListEntitledTextViewTemplate.root.text = entitled
    }

    private fun configureRecyclerView(veggieIngredientList: List<VeggieIngredientAnalysis>){
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(this, linearLayoutManager.orientation)
        val adapter = VeggieItemAdapter(veggieIngredientList)
        viewBinding.activityVeggieRecyclerView.adapter = adapter
        viewBinding.activityVeggieRecyclerView.layoutManager = linearLayoutManager
        viewBinding.activityVeggieRecyclerView.addItemDecoration(dividerItemDecoration)
    }
}