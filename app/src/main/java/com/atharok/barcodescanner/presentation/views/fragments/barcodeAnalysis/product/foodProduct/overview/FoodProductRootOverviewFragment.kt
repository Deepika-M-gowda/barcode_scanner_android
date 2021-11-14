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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.product.foodProduct.overview

import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.ProductBarcodeFragment
import com.atharok.barcodescanner.databinding.FragmentFoodProductRootOverviewBinding
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodProduct
import com.atharok.barcodescanner.domain.entity.product.foodProduct.EcoScore
import com.atharok.barcodescanner.domain.entity.product.foodProduct.NovaGroup
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutriscore
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.AboutBarcodeFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.part.BarcodeContentsFragment
import com.atharok.barcodescanner.presentation.views.fragments.templates.ProductOverviewFragment
import com.atharok.barcodescanner.common.extentions.fixAnimateLayoutChangesInNestedScroll

/**
 * A simple [Fragment] subclass.
 */
class FoodProductRootOverviewFragment : ProductBarcodeFragment<FoodProduct>() {

    private var _binding: FragmentFoodProductRootOverviewBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodProductRootOverviewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodProduct) {

        viewBinding.foodRootOverviewOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureProductOverviewFragment(product)
        applyFragment(viewBinding.foodRootOverviewVeggieAnalysisFrameLayout.id, FoodProductVeggieAnalysisFragment::class, arguments)
        configureQuality(product)
        configureDetails(product)
        configureAboutBarcode()
    }

    // ---- Overview ----

    private fun configureProductOverviewFragment(foodProduct: FoodProduct){

        val fragment = ProductOverviewFragment.newInstance(
            imageUrl = foodProduct.imageFrontUrl,
            title = foodProduct.name ?: getString(R.string.bar_code_type_unknown_product),
            subtitle1 = foodProduct.brands,
            subtitle2 = foodProduct.quantity
        )

        applyFragment(viewBinding.foodRootOverviewOverviewFrameLayout.id, fragment)
    }

    // ---- Quality ----

    private fun configureQuality(foodProduct: FoodProduct){
        val nutriscore: Nutriscore = foodProduct.nutriscore
        val novaGroup: NovaGroup = foodProduct.novaGroup
        val ecoScore: EcoScore = foodProduct.ecoScore

        if(nutriscore == Nutriscore.UNKNOWN && novaGroup == NovaGroup.UNKNOWN && ecoScore == EcoScore.UNKNOWN){
            viewBinding.foodRootOverviewQualityEntitledTextViewTemplate.root.visibility = View.GONE
        }else {

            configureQualityEntitled()

            if (nutriscore != Nutriscore.UNKNOWN) {
                configureNutriScoreFragment(nutriscore)
            } else {
                viewBinding.foodRootOverviewQualityNutriScoreFrameLayout.visibility = View.GONE
            }

            if (novaGroup != NovaGroup.UNKNOWN) {
                configureNovaGroupFragment(novaGroup)
            } else {
                viewBinding.foodRootOverviewQualityNovaGroupFrameLayout.visibility = View.GONE
            }

            if (ecoScore != EcoScore.UNKNOWN) {
                configureEcoScoreFragment(ecoScore)
            } else {
                viewBinding.foodRootOverviewQualityEcoScoreFrameLayout.visibility = View.GONE
            }
        }
    }

    private fun configureQualityEntitled(){
        // Entitled
        val entitled: String = getString(R.string.quality_label)
        viewBinding.foodRootOverviewQualityEntitledTextViewTemplate.root.text = entitled
        viewBinding.foodRootOverviewQualityEntitledTextViewTemplate.root.visibility = View.VISIBLE
    }

    private fun configureNutriScoreFragment(nutriscore: Nutriscore) = configureQualityFragment(
        frameLayout = viewBinding.foodRootOverviewQualityNutriScoreFrameLayout,
        title = getString(R.string.nutriscore_entitled_label),
        subtitle = getString(nutriscore.descriptionStringResource),
        description = getString(R.string.nutriscore_description),
        imageUrl = nutriscore.nutriscoreImageUrl
    )

    private fun configureNovaGroupFragment(noveGroup: NovaGroup) = configureQualityFragment(
        frameLayout = viewBinding.foodRootOverviewQualityNovaGroupFrameLayout,
        title = getString(R.string.nova_group_entitled_label),
        subtitle = getString(noveGroup.descriptionStringResource),
        description = getString(R.string.nova_group_description),
        imageUrl = noveGroup.novaGroupImageUrl
    )

    private fun configureEcoScoreFragment(ecoScore: EcoScore) = configureQualityFragment(
        frameLayout = viewBinding.foodRootOverviewQualityEcoScoreFrameLayout,
        title = getString(R.string.eco_score_entitled_label),
        subtitle = getString(ecoScore.descriptionStringResource),
        description = getString(R.string.eco_score_description),
        imageUrl = ecoScore.ecoScoreImageUrl
    )

    private fun configureQualityFragment(frameLayout: FrameLayout, title: String, subtitle: String, description: String, imageUrl: String?){

        val fragment = FoodProductQualityFragment.newInstance(
            imageUrl = imageUrl,
            title = title,
            subtitle = subtitle,
            description = description
        )

        applyFragment(frameLayout.id, fragment)
    }

    // ---- Details ----

    private fun configureDetails(foodProduct: FoodProduct){

        val categories: String? = foodProduct.categories
        val packaging: String? = foodProduct.packaging
        val stores: String? = foodProduct.stores
        val countries: List<String>? = foodProduct.countriesTagList
        val labels: String? = foodProduct.labels
        val labelsTags: List<String>? = foodProduct.labelsTagList

        if(categories.isNullOrBlank() && packaging.isNullOrBlank() && stores.isNullOrBlank() &&
            countries.isNullOrEmpty() && labels.isNullOrBlank() && labelsTags.isNullOrEmpty()){
            viewBinding.foodRootOverviewDetailsEntitledTextViewTemplate.root.visibility = View.GONE
        }else{

            // Entitled
            val entitled: String = getString(R.string.details_label)
            viewBinding.foodRootOverviewDetailsEntitledTextViewTemplate.root.text = entitled
            viewBinding.foodRootOverviewDetailsEntitledTextViewTemplate.root.visibility = View.VISIBLE

            // Labels Fragment
            if (!labels.isNullOrBlank() || !labelsTags.isNullOrEmpty())
                applyFragment(viewBinding.foodRootOverviewLabelsFrameLayout.id, FoodProductLabelsFragment::class, arguments)

            // Details Fragment
            applyFragment(viewBinding.foodRootOverviewDetailsFrameLayout.id, FoodProductDetailsFragment::class, arguments)
        }
    }

    // ---- Barcode ----

    private fun configureAboutBarcode(){
        val entitled: String = getString(R.string.about_barcode_label)
        viewBinding.foodRootOverviewAboutBarcodeEntitledTextViewTemplate.root.text = entitled

        applyFragment(viewBinding.foodRootOverviewBarcodeContentsFrameLayout.id, BarcodeContentsFragment::class, arguments)
        applyFragment(viewBinding.foodRootOverviewAboutBarcodeFrameLayout.id, AboutBarcodeFragment::class, arguments)
    }

    companion object {
        fun newInstance(foodProduct: FoodProduct) = FoodProductRootOverviewFragment().apply {
            arguments = Bundle().apply {
                putSerializable(PRODUCT_KEY, foodProduct)
            }
        }
    }
}
