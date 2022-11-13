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

package com.atharok.barcodescanner.presentation.views.fragments.templates

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.FragmentExpandableViewBinding
import com.atharok.barcodescanner.databinding.TemplateEntitledViewBinding
import com.atharok.barcodescanner.databinding.TemplateTextViewContentsBinding
import org.koin.android.ext.android.get

/**
 * A simple [Fragment] subclass.
 * Use the [ExpandableViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ExpandableViewFragment : Fragment() {

    private var title: String? = null
    private var contents: CharSequence? = null
    private var drawableResource: Int? = null

    private var _binding: FragmentExpandableViewBinding? = null
    private val viewBinding get() = _binding!!

    private lateinit var headerTemplateBinding: TemplateEntitledViewBinding
    private lateinit var bodyTemplateBinding: TemplateTextViewContentsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString(ENTITLED_KEY)
            contents = it.getCharSequence(CONTENTS_KEY)
            drawableResource = it.getInt(ICON_KEY, DEFAULT_INT_VALUE_BUNDLE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentExpandableViewBinding.inflate(inflater, container, false)
        configureExpandableViewTemplate(inflater)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    private fun configureExpandableViewTemplate(inflater: LayoutInflater) {

        val expandableViewTemplate = viewBinding.fragmentExpandableViewTemplate

        expandableViewTemplate.root.open() // L'ExpandableView est ouvert par défaut
        val parentHeader = expandableViewTemplate.templateExpandableViewHeaderFrameLayout
        val parentBody =  expandableViewTemplate.templateExpandableViewBodyFrameLayout

        headerTemplateBinding = TemplateEntitledViewBinding.inflate(inflater, parentHeader, true)
        bodyTemplateBinding = TemplateTextViewContentsBinding.inflate(inflater, parentBody, true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configureDrawableResource(drawableResource)
        headerTemplateBinding.templateEntitledViewTextView.root.text = title
        bodyTemplateBinding.templateTextViewContentsTextView.text = contents
    }

    private fun configureDrawableResource(drawableResource: Int?){
        if (drawableResource != null && drawableResource != DEFAULT_INT_VALUE_BUNDLE) {
            headerTemplateBinding.templateEntitledViewIconImageView.setImageResource(drawableResource)
        }else{
            headerTemplateBinding.templateEntitledViewIconImageView.visibility = View.GONE
        }
    }

    companion object {

        private const val DEFAULT_INT_VALUE_BUNDLE = -1
        private const val ENTITLED_KEY = "entitledKey"
        private const val CONTENTS_KEY = "contentsKey"
        private const val ICON_KEY = "iconKey"

        @JvmStatic
        fun newInstance(title: String, contents: CharSequence, drawableResource: Int? = null) =
            ExpandableViewFragment().apply {
                arguments = get<Bundle>().apply {
                    putString(ENTITLED_KEY, title)
                    putCharSequence(CONTENTS_KEY, contents)
                    if(drawableResource != null)
                        putInt(ICON_KEY, drawableResource)
                }
            }
    }
}