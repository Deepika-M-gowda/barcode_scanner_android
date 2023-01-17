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

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.databinding.FragmentFormCreateQrCodeContactBinding
import com.atharok.barcodescanner.domain.library.EzvcardBuilder
import com.atharok.barcodescanner.domain.library.VCardReader
import com.atharok.barcodescanner.presentation.intent.createPickContactIntent
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import ezvcard.Ezvcard
import ezvcard.VCard
import ezvcard.parameter.EmailType
import ezvcard.parameter.TelephoneType
import ezvcard.property.Email
import ezvcard.property.Telephone
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class FormCreateQrCodeContactFragment : AbstractFormCreateBarcodeFragment() {

    private val vCardReader: VCardReader by inject()

    private var _binding: FragmentFormCreateQrCodeContactBinding? = null
    private val viewBinding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFormCreateQrCodeContactBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureMailAutoCompleteTextView()
        configurePhoneAutoCompleteTextView()
        configureOnClickImportFromContact()
    }

    override fun generateBarcodeTextFromForm(): String {
        closeVirtualKeyBoard(viewBinding.root)

        val vCard = EzvcardBuilder().apply {

            createStructuredName(
                viewBinding.fragmentFormCreateQrCodeContactNameInputEditText.text.toString(),
                viewBinding.fragmentFormCreateQrCodeContactFirstNameInputEditText.text.toString(),
                getCivil()
            )

            createOrganization(viewBinding.fragmentFormCreateQrCodeContactOrganisationInputEditText.text.toString())
            createUrl(viewBinding.fragmentFormCreateQrCodeContactWebSiteInputEditText.text.toString())
            addEmail(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactMail1InputEditText.text.toString(),
                viewBinding.contactMail1AutoCompleteTextView.text.toString()
            )
            addEmail(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactMail2InputEditText.text.toString(),
                viewBinding.contactMail2AutoCompleteTextView.text.toString()
            )
            addEmail(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactMail3InputEditText.text.toString(),
                viewBinding.contactMail3AutoCompleteTextView.text.toString()
            )
            addPhone(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactPhone1InputEditText.text.toString(),
                viewBinding.contactPhone1AutoCompleteTextView.text.toString()
            )
            addPhone(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactPhone2InputEditText.text.toString(),
                viewBinding.contactPhone2AutoCompleteTextView.text.toString()
            )
            addPhone(requireContext(),
                viewBinding.fragmentFormCreateQrCodeContactPhone3InputEditText.text.toString(),
                viewBinding.contactPhone3AutoCompleteTextView.text.toString()
            )
            createAddress(
                mStreet = viewBinding.fragmentFormCreateQrCodeContactStreetAddressInputEditText.text.toString(),
                mPostalCode = viewBinding.fragmentFormCreateQrCodeContactPostalCodeInputEditText.text.toString(),
                mCity = viewBinding.fragmentFormCreateQrCodeContactCityInputEditText.text.toString(),
                mCountry = viewBinding.fragmentFormCreateQrCodeContactCountryInputEditText.text.toString(),
                mRegion = viewBinding.fragmentFormCreateQrCodeContactRegionInputEditText.text.toString()
            )
            createNote(viewBinding.fragmentFormCreateQrCodeContactNotesInputEditText.text.toString())
        }.build()

        return Ezvcard.write(vCard).prodId(false).go()
    }

    private fun configureMailAutoCompleteTextView(){
        configureAutoCompleteTextView(
            values = arrayOf(
                getString(R.string.spinner_type_work),
                getString(R.string.spinner_type_home),
                getString(R.string.spinner_type_other)
            ),
            autoCompleteTextView1 = viewBinding.contactMail1AutoCompleteTextView,
            autoCompleteTextView2 = viewBinding.contactMail2AutoCompleteTextView,
            autoCompleteTextView3 = viewBinding.contactMail3AutoCompleteTextView,
        )
    }

    private fun configurePhoneAutoCompleteTextView(){
        configureAutoCompleteTextView(
            values = arrayOf(
                getString(R.string.spinner_type_mobile),
                getString(R.string.spinner_type_work),
                getString(R.string.spinner_type_home),
                getString(R.string.spinner_type_fax),
                getString(R.string.spinner_type_other)
            ),
            autoCompleteTextView1 = viewBinding.contactPhone1AutoCompleteTextView,
            autoCompleteTextView2 = viewBinding.contactPhone2AutoCompleteTextView,
            autoCompleteTextView3 = viewBinding.contactPhone3AutoCompleteTextView,
        )
    }

    private fun configureAutoCompleteTextView(
        values: Array<String>,
        autoCompleteTextView1: MaterialAutoCompleteTextView,
        autoCompleteTextView2: MaterialAutoCompleteTextView,
        autoCompleteTextView3: MaterialAutoCompleteTextView,
    ){
        val adapter = ArrayAdapter<String>(requireContext(), R.layout.template_spinner_item, values)
        adapter.setDropDownViewResource(R.layout.template_spinner_item)
        autoCompleteTextView1.setAdapter(adapter)
        autoCompleteTextView1.setText(adapter.getItem(0), false)
        autoCompleteTextView2.setAdapter(adapter)
        autoCompleteTextView2.setText(adapter.getItem(0), false)
        autoCompleteTextView3.setAdapter(adapter)
        autoCompleteTextView3.setText(adapter.getItem(0), false)
    }

    private fun getCivil(): String {
        val civilText = viewBinding.fragmentFormCreateQrCodeContactCivilRadioGroup
            .findViewById<RadioButton>(viewBinding.fragmentFormCreateQrCodeContactCivilRadioGroup.checkedRadioButtonId)
            .text.toString()

        return if(civilText==getString(R.string.qr_code_text_radio_button_label_none)) "" else civilText
    }

    // ---- Importation From Contact ---------------------------------------------------------------

    private fun configureOnClickImportFromContact(){
        viewBinding.fragmentFormCreateQrCodeContactImportationButton.setOnClickListener {
            requestPermission.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private fun fillAllField(vCard: VCard){
        clearAllInputs()

        if (vCard.structuredName != null) {
            viewBinding.fragmentFormCreateQrCodeContactNameInputEditText.setText(vCard.structuredName.family)
            viewBinding.fragmentFormCreateQrCodeContactFirstNameInputEditText.setText(
                vCard.structuredName.given
            )
        }

        if (vCard.organization != null && vCard.organization.values.isNotEmpty())
            viewBinding.fragmentFormCreateQrCodeContactOrganisationInputEditText.setText(
                vCard.organization.values.first()
            )

        if (vCard.urls.isNotEmpty())
            viewBinding.fragmentFormCreateQrCodeContactWebSiteInputEditText.setText(
                vCard.urls.first().value
            )

        if (vCard.emails.isNotEmpty())
            fillEmailField(
                viewBinding.fragmentFormCreateQrCodeContactMail1InputEditText,
                viewBinding.contactMail1AutoCompleteTextView,
                vCard.emails.first()
            )

        if (vCard.emails.size > 1)
            fillEmailField(
                viewBinding.fragmentFormCreateQrCodeContactMail2InputEditText,
                viewBinding.contactMail2AutoCompleteTextView,
                vCard.emails[1]
            )

        if (vCard.emails.size > 2)
            fillEmailField(
                viewBinding.fragmentFormCreateQrCodeContactMail3InputEditText,
                viewBinding.contactMail3AutoCompleteTextView,
                vCard.emails[2]
            )

        if (vCard.telephoneNumbers.isNotEmpty())
            fillPhoneField(
                viewBinding.fragmentFormCreateQrCodeContactPhone1InputEditText,
                viewBinding.contactPhone1AutoCompleteTextView,
                vCard.telephoneNumbers.first()
            )

        if (vCard.telephoneNumbers.size > 1)
            fillPhoneField(
                viewBinding.fragmentFormCreateQrCodeContactPhone2InputEditText,
                viewBinding.contactPhone2AutoCompleteTextView,
                vCard.telephoneNumbers[1]
            )

        if (vCard.telephoneNumbers.size > 2)
            fillPhoneField(
                viewBinding.fragmentFormCreateQrCodeContactPhone3InputEditText,
                viewBinding.contactPhone3AutoCompleteTextView,
                vCard.telephoneNumbers[2]
            )

        if (vCard.addresses.isNotEmpty()) {
            viewBinding.fragmentFormCreateQrCodeContactStreetAddressInputEditText.setText(
                vCard.addresses.first().streetAddress
            )
            viewBinding.fragmentFormCreateQrCodeContactPostalCodeInputEditText.setText(
                vCard.addresses.first().postalCode
            )
            viewBinding.fragmentFormCreateQrCodeContactCityInputEditText.setText(vCard.addresses.first().locality)
            viewBinding.fragmentFormCreateQrCodeContactCountryInputEditText.setText(
                vCard.addresses.first().country
            )
            viewBinding.fragmentFormCreateQrCodeContactRegionInputEditText.setText(vCard.addresses.first().region)
        }

        if (vCard.notes.isNotEmpty())
            viewBinding.fragmentFormCreateQrCodeContactNotesInputEditText.setText(vCard.notes.first().value)
    }

    private fun fillEmailField(editText: TextInputEditText, autoCompleteTextView: MaterialAutoCompleteTextView, email: Email){
        editText.setText(email.value)

        if(email.types.isNotEmpty()) {
            val emailType: EmailType = email.types.first()
            val index = getEmailTypeIndex(emailType)

            autoCompleteTextView.setSelection(index)
        } else autoCompleteTextView.setSelection(2)
    }

    /*private fun fillEmailField(editText: TextInputEditText, spinner: Spinner, email: Email){
        editText.setText(email.value)

        if(email.types.isNotEmpty()) {
            val emailType: EmailType = email.types.first()
            val index = getEmailSpinnerIndex(emailType)

            spinner.setSelection(index)
        } else spinner.setSelection(2)
    }*/

    private fun getEmailTypeIndex(emailType: EmailType): Int = when (emailType) {
        EmailType.WORK -> 0
        EmailType.HOME -> 1
        else -> 2
    }

    /*private fun fillPhoneField(editText: TextInputEditText, spinner: Spinner, phone: Telephone){
        editText.setText(phone.text)

        if(phone.types.isNotEmpty()) {
            val phoneType = phone.types.first()
            val index = getPhoneSpinnerIndex(phoneType)

            spinner.setSelection(index)
        } else spinner.setSelection(4)
    }*/

    private fun fillPhoneField(editText: TextInputEditText, autoCompleteTextView: MaterialAutoCompleteTextView, phone: Telephone){
        editText.setText(phone.text)

        if(phone.types.isNotEmpty()) {
            val phoneType = phone.types.first()
            val index = getPhoneTypeIndex(phoneType)

            autoCompleteTextView.setSelection(index)
        } else autoCompleteTextView.setSelection(4)
    }

    private fun getPhoneTypeIndex(phoneType: TelephoneType): Int = when (phoneType) {
        TelephoneType.CELL -> 0
        TelephoneType.WORK -> 1
        TelephoneType.HOME -> 2
        TelephoneType.FAX -> 3
        else -> 4
    }

    // ---- Contact Activity ----

    /**
     * Gère les données récupérer des contacts.
     */
    private val resultContactActivity: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val uri: Uri? = result?.data?.data
            if (result.resultCode == Activity.RESULT_OK && uri != null){

                // Convertit l'uri du contact en String
                val vCardStr = vCardReader.readVCardFromContactUri(uri)

                val vCard = Ezvcard.parse(vCardStr).first()

                if (vCard != null) {
                    fillAllField(vCard)
                }
            }
        }

    private fun openContactActivity(){
        val intent: Intent = createPickContactIntent()
        resultContactActivity.launch(intent)
    }


    // ---- Permissions ----

    /**
     * Gère le resultat de la demande de permission d'accès aux contacts.
     */
    private val requestPermission: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) openContactActivity() else managePermissionsDenied()
        }

    private fun managePermissionsDenied() {
        Snackbar.make(
            viewBinding.root,
            getString(R.string.snack_bar_message_permission_refused),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    // ----

    private fun clearAllInputs(){

        viewBinding.fragmentFormCreateQrCodeContactNameInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactFirstNameInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactOrganisationInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactWebSiteInputEditText.setText("")

        viewBinding.fragmentFormCreateQrCodeContactMail1InputEditText.setText("")
        viewBinding.contactMail1AutoCompleteTextView.setSelection(0)
        viewBinding.fragmentFormCreateQrCodeContactMail2InputEditText.setText("")
        viewBinding.contactMail2AutoCompleteTextView.setSelection(0)
        viewBinding.fragmentFormCreateQrCodeContactMail3InputEditText.setText("")
        viewBinding.contactMail3AutoCompleteTextView.setSelection(0)

        viewBinding.fragmentFormCreateQrCodeContactPhone1InputEditText.setText("")
        viewBinding.contactPhone1AutoCompleteTextView.setSelection(0)
        viewBinding.fragmentFormCreateQrCodeContactPhone2InputEditText.setText("")
        viewBinding.contactPhone2AutoCompleteTextView.setSelection(0)
        viewBinding.fragmentFormCreateQrCodeContactPhone3InputEditText.setText("")
        viewBinding.contactPhone3AutoCompleteTextView.setSelection(0)

        viewBinding.fragmentFormCreateQrCodeContactStreetAddressInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactPostalCodeInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactCityInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactCountryInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactRegionInputEditText.setText("")
        viewBinding.fragmentFormCreateQrCodeContactNotesInputEditText.setText("")
    }
}
