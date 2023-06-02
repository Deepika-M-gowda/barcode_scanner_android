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

package com.atharok.barcodescanner.presentation.views.fragments.barcodeFormCreator

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.common.extensions.serializable
import com.atharok.barcodescanner.common.utils.BANK_KEY
import com.atharok.barcodescanner.databinding.FragmentBarcodeFormCreatorQrEpcBinding
import com.atharok.barcodescanner.domain.entity.bank.Bank
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.library.Iban
import com.atharok.barcodescanner.presentation.intent.createStartActivityIntent
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBankViewModel
import com.atharok.barcodescanner.presentation.views.activities.BankListActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel

/**
 * A simple [Fragment] subclass.
 */
class BarcodeFormCreatorQrEpcFragment: AbstractBarcodeFormCreatorQrFragment() {

    companion object {
        private const val SERVICE_TAG = "BCD"
        private const val VERSION = "002"
        private const val CHARACTER_SET = "2"
        private const val IDENTIFICATION = "SCT"
    }

    private val databaseBankViewModel: DatabaseBankViewModel by activityViewModel()

    private val iban: Iban by inject()
    private val inputMethodManager: InputMethodManager by inject()
    private val stringBuilder: StringBuilder by lazy { StringBuilder() }

    private var _binding: FragmentBarcodeFormCreatorQrEpcBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBarcodeFormCreatorQrEpcBinding.inflate(inflater, container, false)
        configureMenu()
        configureInputEditTexts()
        configureBankHistoryButton()
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }


    override fun getBarcodeTextFromForm(): String {
        val nameInputEditText = viewBinding.fragmentBarcodeFormCreatorQrEpcNameInputEditText
        val name: String = nameInputEditText.text.toString()
        if(name.isBlank()){
            viewBinding.fragmentBarcodeFormCreatorQrEpcNameErrorTextView.visibility = View.VISIBLE
            nameInputEditText.requestFocus()
            inputMethodManager.showSoftInput(nameInputEditText, InputMethodManager.SHOW_IMPLICIT)
            return ""
        }

        val ibanInputEditText = viewBinding.fragmentBarcodeFormCreatorQrEpcIbanInputEditText
        val ibanText: String = ibanInputEditText.text.toString().uppercase()
        if(!iban.verify(ibanText)) {
            viewBinding.fragmentBarcodeFormCreatorQrEpcIbanErrorTextView.visibility = View.VISIBLE
            ibanInputEditText.requestFocus()
            inputMethodManager.showSoftInput(ibanInputEditText, InputMethodManager.SHOW_IMPLICIT)
            return ""
        }

        val bicText: String = viewBinding.fragmentBarcodeFormCreatorQrEpcBicInputEditText.text.toString()

        saveBankInformationIntoDatabase(name, bicText, ibanText)

        stringBuilder.clear()
        stringBuilder.appendLine(SERVICE_TAG)
        stringBuilder.appendLine(VERSION)
        stringBuilder.appendLine(CHARACTER_SET)
        stringBuilder.appendLine(IDENTIFICATION)
        stringBuilder.appendLine(bicText)
        stringBuilder.appendLine(name)
        stringBuilder.appendLine(ibanText)
        stringBuilder.appendLine(viewBinding.fragmentBarcodeFormCreatorQrEpcAmountInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentBarcodeFormCreatorQrEpcPurposeInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceRefInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceTextInputEditText.text.toString())
        stringBuilder.appendLine(viewBinding.fragmentBarcodeFormCreatorQrEpcInformationInputEditText.text.toString())

        return stringBuilder.toString().trim()
    }

    private fun configureInputEditTexts() {

        viewBinding.fragmentBarcodeFormCreatorQrEpcIbanInputEditText.addTextChangedListener {
            val errorTextView = viewBinding.fragmentBarcodeFormCreatorQrEpcIbanErrorTextView
            if(errorTextView.visibility == View.VISIBLE) {
                if (iban.verify(it.toString())) {
                    errorTextView.visibility = View.GONE
                }
            }
        }

        viewBinding.fragmentBarcodeFormCreatorQrEpcNameInputEditText.addTextChangedListener {
            if(!it.isNullOrEmpty()){
                viewBinding.fragmentBarcodeFormCreatorQrEpcNameErrorTextView.visibility = View.GONE
            }
        }

        // Si l'un des deux inputEditText suivant contient du texte, on enlève l'autre car seulement un des 2 doit contenir une valeur

        viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceRefInputEditText.addTextChangedListener {
            viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceTextInputEditText.apply {
                visibility = if(it.isNullOrEmpty())View.VISIBLE else View.GONE
            }
        }

        viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceTextInputEditText.addTextChangedListener {
            viewBinding.fragmentBarcodeFormCreatorQrEpcRemittanceRefInputEditText.apply {
                visibility = if(it.isNullOrEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    // ---- Bank History ----

    private fun saveBankInformationIntoDatabase(name: String, bic: String, iban: String) {
        val bank = Bank(name, bic, iban)
        databaseBankViewModel.insertBank(bank)
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.serializable(BANK_KEY, Bank::class.java)?.let {
                viewBinding.fragmentBarcodeFormCreatorQrEpcBicInputEditText.setText(it.bic)
                viewBinding.fragmentBarcodeFormCreatorQrEpcNameInputEditText.setText(it.name)
                viewBinding.fragmentBarcodeFormCreatorQrEpcIbanInputEditText.setText(it.iban)
            }
        }
    }

    private fun configureBankHistoryButton() {
        viewBinding.fragmentBarcodeFormCreatorQrEpcOnActivityResultButton.setOnClickListener {
            val intent = createStartActivityIntent(requireContext(), BankListActivity::class)
            resultLauncher.launch(intent)
        }
    }

    override fun getBarcodeType(): BarcodeType = BarcodeType.TEXT
}