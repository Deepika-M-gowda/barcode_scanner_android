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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentFoodAnalysisQualityBinding
import com.atharok.barcodescanner.databinding.TemplateProductQualityBinding
import com.atharok.barcodescanner.databinding.TemplateTextViewContentsBinding
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 */
class FoodAnalysisQualityFragment : BaseFragment() {

    private var _binding: FragmentFoodAnalysisQualityBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerProductQualityTemplateBinding: TemplateProductQualityBinding
    private lateinit var bodyTextViewTemplateBinding: TemplateTextViewContentsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFoodAnalysisQualityBinding.inflate(inflater, container, false)
        configureProductQualityTemplates(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureProductQualityTemplates(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentFoodAnalysisQualityExpandableViewTemplate

        expandableViewTemplate.root.close() // L'ExpandableView est fermée par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody = expandableViewTemplate.templateExpandableViewBodyFrameLayout

        headerProductQualityTemplateBinding = TemplateProductQualityBinding.inflate(inflater, parentHeader, true)
        bodyTextViewTemplateBinding = TemplateTextViewContentsBinding.inflate(inflater, parentBody, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureViews()
    }

    private fun configureViews(){

        val cardView = viewBinding.root

        arguments?.let {
            val title = it.getString(TITLE_KEY)
            val subtitle = it.getString(SUBTITLE_KEY)
            val description = it.getString(DESCRIPTION_KEY)
            val drawableResource = it.getInt(DRAWABLE_RESOURCE_KEY)

            if (!title.isNullOrBlank() || !subtitle.isNullOrBlank() || !description.isNullOrBlank() || drawableResource!=-1) {
                cardView.visibility = View.VISIBLE

                headerProductQualityTemplateBinding.apply {
                    templateProductQualityEntitled.text = title
                    templateProductQualityDescription.text = subtitle
                    templateProductQualityImageView.setImageResource(drawableResource)
                }
                bodyTextViewTemplateBinding.root.apply {
                    text = description
                    setTextIsSelectable(true)
                }

            } else {
                cardView.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val DRAWABLE_RESOURCE_KEY = "drawableResourceKey"
        private const val TITLE_KEY = "titleKey"
        private const val SUBTITLE_KEY = "subtitleKey"
        private const val DESCRIPTION_KEY = "descriptionKey"

        @JvmStatic
        fun newInstance(drawableRes: Int, title: String, subtitle: String, description: String? = null) =
            FoodAnalysisQualityFragment().apply {
                arguments = get<Bundle>().apply {

                    putString(TITLE_KEY, title)
                    putString(SUBTITLE_KEY, subtitle)
                    putInt(DRAWABLE_RESOURCE_KEY, drawableRes)

                    if(description != null)
                        putString(DESCRIPTION_KEY, description)
                }
            }
    }
}