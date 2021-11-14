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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import com.atharok.barcodescanner.presentation.views.fragments.BaseFragment
import com.atharok.barcodescanner.databinding.FragmentFormCreateBarcodeHeaderBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.common.utils.BARCODE_TYPE_ENUM_KEY

/**
 * A simple [Fragment] subclass.
 */
class FormCreateBarcodeHeaderFragment : BaseFragment() {

    private var _binding: FragmentFormCreateBarcodeHeaderBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateBarcodeHeaderBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.takeIf {
            it.containsKey(BARCODE_TYPE_ENUM_KEY) && it.getSerializable(BARCODE_TYPE_ENUM_KEY) is BarcodeFormatDetails
        }?.apply {
            configureView(getSerializable(BARCODE_TYPE_ENUM_KEY) as BarcodeFormatDetails)
        }
    }

    /**
     * On affiche l'item de la RecyclerView que l'on a séléctionné dans l'Activity:
     * "QrCodeGeneratorListActivity" servant ici de Header à l'Activity. De plus, cela permet
     * de faire une animation de transition propre entre "QrCodeGeneratorListActivity"
     * et "QrCodeGeneratorActivity".
     */
    private fun configureView(barcodeFormatDetails: BarcodeFormatDetails){

        val imageView = viewBinding.fragmentFormCreateBarcodeHeaderItem.recyclerViewItemQrCreatorImageView
        val textView = viewBinding.fragmentFormCreateBarcodeHeaderItem.recyclerViewItemQrCreatorTextView

        // On affiche les données de l'item choisie dans l'Activity: QrCodeGeneratorListActivity
        textView.text = getString(barcodeFormatDetails.stringResource)
        imageView.setImageResource(barcodeFormatDetails.drawableResource)
    }
}