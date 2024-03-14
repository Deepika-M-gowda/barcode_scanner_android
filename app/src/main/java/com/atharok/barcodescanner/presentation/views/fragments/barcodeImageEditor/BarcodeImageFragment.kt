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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeImageEditor

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeImageBinding
import com.atharok.barcodescanner.domain.library.BarcodeImageGeneratorProperties
import com.atharok.barcodescanner.domain.resources.Resource
import com.atharok.barcodescanner.presentation.viewmodel.ImageManagerViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class BarcodeImageFragment : Fragment() {

    private var _binding: FragmentBarcodeImageBinding? = null
    private val viewBinding get() = _binding!!

    var bitmap: Bitmap? = null
        private set

    private val imageManagerViewModel: ImageManagerViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarcodeImageBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.serializable(
            BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY, BarcodeImageGeneratorProperties::class.java
        )?.let { properties ->
            configureBarcodeBitmap()
            createBarcodeBitmap(properties)
        } ?: run {
            viewBinding.fragmentBarcodeImageProgressBar.visibility = View.GONE
        }
    }

    private fun configureBarcodeBitmap() {
        val progressBar = viewBinding.fragmentBarcodeImageProgressBar
        val barcodeImageView = viewBinding.fragmentBarcodeImageImageView

        imageManagerViewModel.getBitmap().observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Progress -> progressBar.visibility = View.VISIBLE
                is Resource.Success -> {
                    bitmap = it.data
                    bitmap?.let { localBitmap ->
                        barcodeImageView.setImageBitmap(localBitmap)
                    }
                    progressBar.visibility = View.GONE
                }
                is Resource.Failure -> progressBar.visibility = View.GONE
            }
        }
    }

    private fun createBarcodeBitmap(properties: BarcodeImageGeneratorProperties) {
        viewBinding.fragmentBarcodeImageProgressBar.visibility = View.VISIBLE
        imageManagerViewModel.createBitmap(properties)
    }

    fun generateNewBarcodeBitmap(properties: BarcodeImageGeneratorProperties) {
        arguments = Bundle().apply {
            putSerializable(BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY, properties)
        }
        if(activity != null) createBarcodeBitmap(properties)
    }

    companion object {
        @JvmStatic
        fun newInstance(properties: BarcodeImageGeneratorProperties) =
            BarcodeImageFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(BARCODE_IMAGE_GENERATOR_PROPERTIES_KEY, properties)
                }
            }
    }
}