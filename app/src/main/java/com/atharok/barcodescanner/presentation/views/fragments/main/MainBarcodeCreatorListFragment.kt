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

import android.animation.LayoutTransition
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.databinding.TemplateItemBarcodeCreatorBinding
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.common.utils.BARCODE_TYPE_ENUM_KEY
import com.atharok.barcodescanner.databinding.FragmentMainBarcodeCreatorListBinding
import com.atharok.barcodescanner.presentation.views.activities.BarcodeCreatorFormsActivity

/**
 * A simple [Fragment] subclass.
 */
class MainBarcodeCreatorListFragment : Fragment() {

    private var _binding: FragmentMainBarcodeCreatorListBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMainBarcodeCreatorListBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewBinding.fragmentMainCreateBarcodeListOuterView.layoutTransition?.setAnimateParentHierarchy(false)
        viewBinding.fragmentMainCreateBarcodeListOuterView.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

        configureQrItemListView()
        configureBarcodeItemListView()
    }

    private fun configureQrItemListView(){

        // ---- On génère un array des types de QR Code à partir de l'enumeration ----
        val qrCodeFormatDetails: List<BarcodeFormatDetails> = listOf(
            BarcodeFormatDetails.QR_TEXT,
            BarcodeFormatDetails.QR_AGENDA,
            BarcodeFormatDetails.QR_CONTACT,
            BarcodeFormatDetails.QR_LOCALISATION,
            BarcodeFormatDetails.QR_MAIL,
            BarcodeFormatDetails.QR_PHONE,
            BarcodeFormatDetails.QR_SMS,
            BarcodeFormatDetails.QR_URL,
            BarcodeFormatDetails.QR_WIFI
        )

        configureItemListView(viewBinding.fragmentMainCreateBarcodeListQrLinearLayout, qrCodeFormatDetails)
    }

    private fun configureBarcodeItemListView(){

        // ---- On génère un array des types de QR Code à partir de l'enumeration ----
        val barcodeFormatDetails: List<BarcodeFormatDetails> = listOf(
            BarcodeFormatDetails.DATA_MATRIX,
            BarcodeFormatDetails.PDF_417,
            BarcodeFormatDetails.AZTEC,
            BarcodeFormatDetails.EAN_13,
            BarcodeFormatDetails.EAN_8,
            BarcodeFormatDetails.UPC_A,
            BarcodeFormatDetails.UPC_E,
            BarcodeFormatDetails.CODE_128,
            BarcodeFormatDetails.CODE_93,
            BarcodeFormatDetails.CODE_39,
            BarcodeFormatDetails.CODABAR,
            BarcodeFormatDetails.ITF
        )

        configureItemListView(viewBinding.fragmentMainCreateBarcodeListBarcodeLinearLayout, barcodeFormatDetails)
    }

    private fun configureItemListView(linearLayout: LinearLayout, barCodeTypeList: List<BarcodeFormatDetails>){

        val inflater = LayoutInflater.from(requireContext())

        barCodeTypeList.forEach {
            val itemViewBinding = TemplateItemBarcodeCreatorBinding.inflate(inflater, linearLayout, false)

            itemViewBinding.templateItemBarcodeCreatorTextView.text = getString(it.stringResource)
            itemViewBinding.templateItemBarcodeCreatorImageView.setImageResource(it.drawableResource)

            itemViewBinding.root.setOnClickListener { _ ->
                onClickItem(it)
            }

            linearLayout.addView(itemViewBinding.root)
        }
    }

    private fun onClickItem(barcodeFormatDetails: BarcodeFormatDetails) {
        val intent = Intent(requireContext(), BarcodeCreatorFormsActivity::class.java)
        intent.putExtra(BARCODE_TYPE_ENUM_KEY, barcodeFormatDetails)

        /*val options = ActivityOptions.makeSceneTransitionAnimation(
            requireActivity(), view, getString(R.string.animation_activity_transition))*/

        startActivity(intent/*, options?.toBundle()*/)
    }
}