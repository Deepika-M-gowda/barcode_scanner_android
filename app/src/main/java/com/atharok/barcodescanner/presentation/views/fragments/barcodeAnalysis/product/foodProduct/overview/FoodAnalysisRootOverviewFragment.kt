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
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.abstracts.BarcodeAnalysisFragment
import com.atharok.barcodescanner.domain.entity.product.foodProduct.FoodBarcodeAnalysis
import com.atharok.barcodescanner.domain.entity.product.foodProduct.EcoScore
import com.atharok.barcodescanner.domain.entity.product.foodProduct.NovaGroup
import com.atharok.barcodescanner.domain.entity.product.foodProduct.Nutriscore
import com.atharok.barcodescanner.common.utils.PRODUCT_KEY
import com.atharok.barcodescanner.presentation.views.fragments.templates.ProductOverviewFragment
import com.atharok.barcodescanner.common.extensions.fixAnimateLayoutChangesInNestedScroll
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisRootOverviewBinding
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.defaultBarcode.root.BarcodeAnalysisInformationFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisRootOverviewFragment : BarcodeAnalysisFragment<FoodBarcodeAnalysis>() {

    private var _binding: FragmentFoodAnalysisRootOverviewBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisRootOverviewBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun start(product: FoodBarcodeAnalysis) {

        viewBinding.fragmentFoodAnalysisRootOverviewOuterView.fixAnimateLayoutChangesInNestedScroll()

        configureProductOverviewFragment(product)
        applyFragment(viewBinding.fragmentFoodAnalysisRootOverviewVeggieAnalysisFrameLayout.id, FoodAnalysisVeggieFragment::class, arguments)
        configureQuality(product)
        configureDetails(product)
        configureAboutBarcodeFragment()
    }

    // ---- Overview ----

    private fun configureProductOverviewFragment(foodProduct: FoodBarcodeAnalysis){

        val fragment = ProductOverviewFragment.newInstance(
            imageUrl = foodProduct.imageFrontUrl,
            title = foodProduct.name ?: getString(R.string.bar_code_type_unknown_product),
            subtitle1 = foodProduct.brands,
            subtitle2 = foodProduct.quantity
        )

        applyFragment(viewBinding.fragmentFoodAnalysisRootOverviewOverviewFrameLayout.id, fragment)
    }

    // ---- Quality ----

    private fun configureQuality(foodProduct: FoodBarcodeAnalysis){
        val nutriscore: Nutriscore = foodProduct.nutriscore
        val novaGroup: NovaGroup = foodProduct.novaGroup
        val ecoScore: EcoScore = foodProduct.ecoScore

        if(nutriscore == Nutriscore.UNKNOWN && novaGroup == NovaGroup.UNKNOWN && ecoScore == EcoScore.UNKNOWN){
            viewBinding.fragmentFoodAnalysisRootOverviewQualityEntitledTextViewTemplate.root.visibility = View.GONE
        }else {

            configureQualityEntitled()

            if (nutriscore != Nutriscore.UNKNOWN) {
                configureNutriScoreFragment(nutriscore)
            } else {
                viewBinding.fragmentFoodAnalysisRootOverviewQualityNutriScoreFrameLayout.visibility = View.GONE
            }

            if (novaGroup != NovaGroup.UNKNOWN) {
                configureNovaGroupFragment(novaGroup)
            } else {
                viewBinding.fragmentFoodAnalysisRootOverviewQualityNovaGroupFrameLayout.visibility = View.GONE
            }

            if (ecoScore != EcoScore.UNKNOWN) {
                configureEcoScoreFragment(ecoScore)
            } else {
                viewBinding.fragmentFoodAnalysisRootOverviewQualityEcoScoreFrameLayout.visibility = View.GONE
            }
        }
    }

    private fun configureQualityEntitled(){
        // Entitled
        val entitled: String = getString(R.string.quality_label)
        viewBinding.fragmentFoodAnalysisRootOverviewQualityEntitledTextViewTemplate.root.text = entitled
        viewBinding.fragmentFoodAnalysisRootOverviewQualityEntitledTextViewTemplate.root.visibility = View.VISIBLE
    }

    private fun configureNutriScoreFragment(nutriscore: Nutriscore) = configureQualityFragment(
        frameLayout = viewBinding.fragmentFoodAnalysisRootOverviewQualityNutriScoreFrameLayout,
        title = getString(R.string.nutriscore_entitled_label),
        subtitle = getString(nutriscore.descriptionStringResource),
        description = getString(R.string.nutriscore_description),
        imageUrl = nutriscore.nutriscoreImageUrl
    )

    private fun configureNovaGroupFragment(noveGroup: NovaGroup) = configureQualityFragment(
        frameLayout = viewBinding.fragmentFoodAnalysisRootOverviewQualityNovaGroupFrameLayout,
        title = getString(R.string.nova_group_entitled_label),
        subtitle = getString(noveGroup.descriptionStringResource),
        description = getString(R.string.nova_group_description),
        imageUrl = noveGroup.novaGroupImageUrl
    )

    private fun configureEcoScoreFragment(ecoScore: EcoScore) = configureQualityFragment(
        frameLayout = viewBinding.fragmentFoodAnalysisRootOverviewQualityEcoScoreFrameLayout,
        title = getString(R.string.eco_score_entitled_label),
        subtitle = getString(ecoScore.descriptionStringResource),
        description = getString(R.string.eco_score_description),
        imageUrl = ecoScore.ecoScoreImageUrl
    )

    private fun configureQualityFragment(frameLayout: FrameLayout, title: String, subtitle: String, description: String, imageUrl: String?){

        val fragment = FoodAnalysisQualityFragment.newInstance(
            imageUrl = imageUrl,
            title = title,
            subtitle = subtitle,
            description = description
        )

        applyFragment(frameLayout.id, fragment)
    }

    // ---- Details ----

    private fun configureDetails(foodProduct: FoodBarcodeAnalysis){

        val categories: String? = foodProduct.categories
        val packaging: String? = foodProduct.packaging
        val stores: String? = foodProduct.stores
        val countries: List<String>? = foodProduct.countriesTagList
        val labels: String? = foodProduct.labels
        val labelsTags: List<String>? = foodProduct.labelsTagList

        if(categories.isNullOrBlank() && packaging.isNullOrBlank() && stores.isNullOrBlank() &&
            countries.isNullOrEmpty() && labels.isNullOrBlank() && labelsTags.isNullOrEmpty()){
            viewBinding.fragmentFoodAnalysisRootOverviewDetailsEntitledTextViewTemplate.root.visibility = View.GONE
        }else{

            // Entitled
            val entitled: String = getString(R.string.details_label)
            viewBinding.fragmentFoodAnalysisRootOverviewDetailsEntitledTextViewTemplate.root.text = entitled
            viewBinding.fragmentFoodAnalysisRootOverviewDetailsEntitledTextViewTemplate.root.visibility = View.VISIBLE

            // Labels Fragment
            if (!labels.isNullOrBlank() || !labelsTags.isNullOrEmpty())
                applyFragment(viewBinding.fragmentFoodAnalysisRootOverviewLabelsFrameLayout.id, FoodAnalysisLabelsFragment::class, arguments)

            // Details Fragment
            applyFragment(viewBinding.fragmentFoodAnalysisRootOverviewDetailsFrameLayout.id, FoodAnalysisDetailsFragment::class, arguments)
        }
    }

    // ---- Barcode ----

    private fun configureAboutBarcodeFragment() = applyFragment(
        containerViewId = viewBinding.fragmentFoodAnalysisRootOverviewAboutBarcodeFrameLayout.id,
        fragmentClass = BarcodeAnalysisInformationFragment::class,
        args = arguments
    )

    companion object {
        fun newInstance(foodProduct: FoodBarcodeAnalysis) = FoodAnalysisRootOverviewFragment().apply {
            arguments = get<Bundle>().apply {
                putSerializable(PRODUCT_KEY, foodProduct)
            }
        }
    }
}
