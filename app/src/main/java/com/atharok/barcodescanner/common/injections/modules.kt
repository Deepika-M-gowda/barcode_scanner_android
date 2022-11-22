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

package com.atharok.barcodescanner.common.injections

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.setTextColorFromAttrRes
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.data.api.OpenBeautyFactsService
import com.atharok.barcodescanner.data.api.OpenFoodFactsService
import com.atharok.barcodescanner.data.api.OpenLibraryService
import com.atharok.barcodescanner.data.api.OpenPetFoodFactsService
import com.atharok.barcodescanner.data.database.AppDatabase
import com.atharok.barcodescanner.data.database.BarcodeDao
import com.atharok.barcodescanner.data.database.createBarcodeDao
import com.atharok.barcodescanner.data.database.createDatabase
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.network.createApiClient
import com.atharok.barcodescanner.data.repositories.*
import com.atharok.barcodescanner.domain.entity.action.ActionEnum
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.library.*
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithNewLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithOldLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.data.WifiSetupData
import com.atharok.barcodescanner.domain.repositories.*
import com.atharok.barcodescanner.domain.usecases.DatabaseUseCase
import com.atharok.barcodescanner.domain.usecases.ExternalFoodProductDependencyUseCase
import com.atharok.barcodescanner.domain.usecases.ProductUseCase
import com.atharok.barcodescanner.presentation.intent.*
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ExternalFileViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ProductViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.ActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.SimpleActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.WifiActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogIntentActions.EmailActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogIntentActions.PhoneActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogIntentActions.SmsActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.dialogSearchActions.*
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.intentActions.AgendaActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.intentActions.ContactActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.intentActions.LocalisationActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.intentActions.UrlActionsFragment
import com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.*
import com.atharok.barcodescanner.presentation.views.fragments.main.MainBarcodeCreatorListFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainHistoryFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainScannerFragment
import com.atharok.barcodescanner.presentation.views.fragments.main.MainSettingsFragment
import com.google.android.material.chip.Chip
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.Result
import com.google.zxing.client.result.*
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.*
import kotlin.reflect.KClass

val appModules by lazy {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf<Module>(
            androidModule,
            libraryModule,
            libraryApi29Module,
            viewModelModule,
            useCaseModule,
            repositoryModule,
            dataModule,
            scopesModule,
            fragmentsModule,
            viewsModule
        )
    } else {
        listOf<Module>(
            androidModule,
            libraryModule,
            viewModelModule,
            useCaseModule,
            repositoryModule,
            dataModule,
            scopesModule,
            fragmentsModule,
            viewsModule
        )
    }
}

val androidModule: Module = module {
    single<BeepManager> { BeepManager() }
    single<VibratorAppCompat> { VibratorAppCompat(androidApplication().applicationContext) }
    single<ConnectivityManager> { androidApplication().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single<ClipboardManager> { androidApplication().applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    single<InputMethodManager> { androidApplication().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    single<WifiManager> { androidApplication().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    factory<Barcode> { (contents: String, formatName: String) ->
        Barcode(contents, formatName, System.currentTimeMillis())
    }

    factory { Bundle() }

    // ---- Intent ----

    factory<Intent>(named(INTENT_START_ACTIVITY)) { (kClass: KClass<*>) ->
        createStartActivityIntent(androidContext(), kClass)
    }

    factory<Intent>(named(INTENT_PICK_IMAGE)) {
        createPickImageIntent()
    }

    factory<Intent>(named(INTENT_PICK_CONTACT)) {
        createPickContactIntent()
    }

    factory<Intent>(named(INTENT_PICK_WIFI_NETWORK)) {
        createPickWifiNetworkIntent()
    }

    factory<Intent>(named(INTENT_WIFI_ADD_NETWORKS)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            createWifiAddNetworksIntent()
        } else {
            Intent()
        }
    }

    factory<Intent>(named(INTENT_ACTION_CREATE_IMAGE)) {
        val date = get<Date>()
        val simpleDateFormat = get<SimpleDateFormat> { parametersOf("yyyy-MM-dd-HH-mm-ss") }
        val dateNameStr = simpleDateFormat.format(date)
        val name = "barcode_$dateNameStr"
        createActionCreateImageIntent(name)
    }

    factory<Intent>(named(INTENT_SHARE_TEXT)) { (text: String) ->
        createShareTextIntent(androidContext(), text)
    }

    factory<Intent>(named(INTENT_SHARE_IMAGE)) { (uri: Uri) ->
        createShareImageIntent(androidContext(), uri)
    }

    factory<Intent>(named(INTENT_ADD_AGENDA)) { (parsedResult: CalendarParsedResult) ->
        createAddAgendaIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_ADD_CONTACT)) { (parsedResult: AddressBookParsedResult) ->
        createAddContactIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_ADD_EMAIL)) { (parsedResult: EmailAddressParsedResult) ->
        createAddEmailIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_ADD_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
        createAddPhoneNumberIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_ADD_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->
        createAddSmsNumberIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_SEND_EMAIL)) { (parsedResult: EmailAddressParsedResult) ->
        createSendEmailIntent(androidContext(), parsedResult)
    }

    factory<Intent>(named(INTENT_CALL_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
        createCallPhoneNumberIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_CALL_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->
        createCallSmsNumberIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_SEND_SMS_TO_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
        createSendSmsToPhoneNumberIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_SEND_SMS_TO_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->
        createSendSmsToSmsNumberIntent(parsedResult)
    }

    /*factory<Intent>(named(INTENT_SEARCH_LOCALISATION)) { (parsedResult: GeoParsedResult) ->
        createSearchLocalisationIntent(parsedResult)
    }

    factory<Intent>(named(INTENT_SEARCH_URL)) { (parsedResult: URIParsedResult) ->
        createSearchUrlIntent(parsedResult)
    }*/

    factory<Intent>(named(INTENT_SEARCH_URL)) { (url: String) ->
        createSearchUrlIntent(url)
    }
}

val libraryModule: Module = module {
    single<SettingsManager> { SettingsManager(androidContext()) }
    single<BitmapBarcodeAnalyser>{ BitmapBarcodeAnalyser() }
    single<BarcodeFormatChecker> { BarcodeFormatChecker() }
    single<VCardReader> { VCardReader(androidContext()) }
    single<MultiFormatWriter> { MultiFormatWriter() }
    //single<BarcodeEncoder> { BarcodeEncoder() }
    single<BitmapBarcodeGenerator> { BitmapBarcodeGenerator(get<MultiFormatWriter>()/*, get<BarcodeEncoder>()*/) }
    single<BitmapRecorder> { BitmapRecorder(androidContext()) }
    single<BitmapSharer> { BitmapSharer(androidContext()) }
    single<WifiSetupWithOldLibrary> { WifiSetupWithOldLibrary() }
    single<Iban> { Iban() }

    /*factory<BeepManager> { (activity: Activity) ->
        //val settingsManager = get<SettingsManager>()
        BeepManager(activity)/*.apply {
            isBeepEnabled = settingsManager.useBipScan
            isVibrateEnabled = settingsManager.useVibrateScan
        }*/
    }*/

    factory { Date() }
    factory { (pattern: String) -> SimpleDateFormat(pattern, Locale.getDefault()) }
}

@RequiresApi(Build.VERSION_CODES.Q)
val libraryApi29Module: Module = module {
    single<WifiSetupWithNewLibrary> { WifiSetupWithNewLibrary() }
}

val viewModelModule: Module = module {
    viewModel {
        ProductViewModel(get<ProductUseCase>())
    }

    viewModel {
        DatabaseViewModel(get<DatabaseUseCase>())
    }

    viewModel {
        ExternalFileViewModel(get<ExternalFoodProductDependencyUseCase>())
    }
}

val useCaseModule: Module = module {
    single<ProductUseCase> {
        ProductUseCase(
            foodProductRepository = get<FoodProductRepository>(),
            beautyProductRepository = get<BeautyProductRepository>(),
            petFoodProductRepository = get<PetFoodProductRepository>(),
            bookProductRepository = get<BookProductRepository>()
        )
    }

    single<DatabaseUseCase> {
        DatabaseUseCase(get<BarcodeRepository>())
    }

    single<ExternalFoodProductDependencyUseCase> {
        ExternalFoodProductDependencyUseCase(
            labelsRepository = get<LabelsRepository>(),
            additivesRepository = get<AdditivesRepository>(),
            allergensRepository = get<AllergensRepository>(),
            countriesRepository = get<CountriesRepository>()
        )
    }
}

val repositoryModule: Module = module {

    single<FoodProductRepository> {
        FoodProductRepositoryImpl(get<OpenFoodFactsService>())
    }

    single<BeautyProductRepository> {
        BeautyProductRepositoryImpl(get<OpenBeautyFactsService>())
    }

    single<PetFoodProductRepository> {
        PetFoodProductRepositoryImpl(get<OpenPetFoodFactsService>())
    }

    single<BookProductRepository> {
        BookProductRepositoryImpl(get<OpenLibraryService>())
    }

    single<BarcodeRepository> {
        BarcodeRepositoryImpl(get<BarcodeDao>())
    }

    single<LabelsRepository> {
        LabelsRepositoryImpl(get<FileFetcher>())
    }

    single<AdditiveResponseRepository> {
        AdditiveResponseRepository(get<FileFetcher>())
    }

    single<AdditiveClassRepository> {
        AdditiveClassRepositoryImpl(androidContext(), get<FileFetcher>())
    }

    single<AdditivesRepository> {
        AdditivesRepositoryImpl(get<AdditiveResponseRepository>(), get<AdditiveClassRepository>())
    }

    single<AllergensRepository> {
        AllergensRepositoryImpl(get<FileFetcher>())
    }

    single<CountriesRepository> {
        CountriesRepositoryImpl(get<FileFetcher>())
    }
}

val dataModule: Module = module {

    single<OpenFoodFactsService> {
        val baseUrl = androidContext().getString(R.string.base_api_open_food_facts_url)
        createApiClient(androidContext(), baseUrl).create(OpenFoodFactsService::class.java)
    }

    single<OpenBeautyFactsService> {
        val baseUrl = androidContext().getString(R.string.base_api_open_beauty_facts_url)
        createApiClient(androidContext(), baseUrl).create(OpenBeautyFactsService::class.java)
    }

    single<OpenPetFoodFactsService> {
        val baseUrl = androidContext().getString(R.string.base_api_open_pet_food_facts_url)
        createApiClient(androidContext(), baseUrl).create(OpenPetFoodFactsService::class.java)
    }

    single<OpenLibraryService> {
        val baseUrl = androidContext().getString(R.string.base_api_open_library_url)
        createApiClient(androidContext(), baseUrl).create(OpenLibraryService::class.java)
    }

    single<AppDatabase> {
        createDatabase(androidContext())
    }

    single<BarcodeDao> {
        createBarcodeDao(get<AppDatabase>())
    }

    single<FileFetcher> { FileFetcher(androidContext()) }
}

val fragmentsModule = module {

    factory { MainScannerFragment() }
    factory { MainHistoryFragment() }
    factory { MainBarcodeCreatorListFragment() }
    factory { MainSettingsFragment() }

    factory { FormCreateQrCodeTextFragment() }
    factory { FormCreateQrCodeUrlFragment() }
    factory { FormCreateQrCodeContactFragment() }
    factory { FormCreateQrCodeEpcFragment() }
    factory { FormCreateQrCodeMailFragment() }
    factory { FormCreateQrCodeSmsFragment() }
    factory { FormCreateQrCodePhoneFragment() }
    factory { FormCreateQrCodeLocalisationFragment() }
    factory { FormCreateQrCodeAgendaFragment() }
    factory { FormCreateQrCodeWifiFragment() }
    factory { FormCreateBarcodeFragment() }

    factory<AbstractFormCreateBarcodeFragment> { (type: BarcodeFormatDetails) ->

        when(type){
            BarcodeFormatDetails.QR_TEXT -> get<FormCreateQrCodeTextFragment>()
            BarcodeFormatDetails.QR_URL -> get<FormCreateQrCodeUrlFragment>()
            BarcodeFormatDetails.QR_CONTACT -> get<FormCreateQrCodeContactFragment>()
            BarcodeFormatDetails.QR_EPC -> get<FormCreateQrCodeEpcFragment>()
            BarcodeFormatDetails.QR_MAIL -> get<FormCreateQrCodeMailFragment>()
            BarcodeFormatDetails.QR_SMS -> get<FormCreateQrCodeSmsFragment>()
            BarcodeFormatDetails.QR_PHONE -> get<FormCreateQrCodePhoneFragment>()
            BarcodeFormatDetails.QR_LOCALISATION -> get<FormCreateQrCodeLocalisationFragment>()
            BarcodeFormatDetails.QR_AGENDA -> get<FormCreateQrCodeAgendaFragment>()
            BarcodeFormatDetails.QR_WIFI -> get<FormCreateQrCodeWifiFragment>()
            else -> {

                get<FormCreateBarcodeFragment>().apply {
                    arguments = get<Bundle>().apply {
                        putSerializable(BARCODE_FORMAT_KEY, type.format)
                    }
                }
            }
        }
    }

    factory<KClass<out ActionsFragment>> { (barcodeType: BarcodeType) ->

        when(barcodeType){
            BarcodeType.AGENDA -> AgendaActionsFragment::class
            BarcodeType.CONTACT -> ContactActionsFragment::class
            BarcodeType.LOCALISATION -> LocalisationActionsFragment::class
            BarcodeType.MAIL -> EmailActionsFragment::class
            BarcodeType.PHONE -> PhoneActionsFragment::class
            BarcodeType.SMS -> SmsActionsFragment::class
            BarcodeType.TEXT -> SimpleActionsFragment::class
            BarcodeType.URL -> UrlActionsFragment::class
            BarcodeType.WIFI -> WifiActionsFragment::class
            BarcodeType.FOOD -> FoodActionsFragment::class
            BarcodeType.PET_FOOD -> PetFoodActionsFragment::class
            BarcodeType.BEAUTY -> BeautyActionsFragment::class
            BarcodeType.BOOK -> BookActionsFragment::class
            BarcodeType.INDUSTRIAL -> SimpleActionsFragment::class
            BarcodeType.MATRIX -> SimpleActionsFragment::class
            BarcodeType.UNKNOWN -> SimpleActionsFragment::class
            BarcodeType.UNKNOWN_PRODUCT -> ProductActionsFragment::class
        }
    }
}

val viewsModule = module {

    factory<Chip> { (activity: Activity, text: String) ->

        Chip(activity).apply {
            id = View.NO_ID
            this.text = text
            setEnsureMinTouchTargetSize(false)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            if (Build.VERSION.SDK_INT < 23) {
                setTextAppearance(context, R.style.ChipText);
            } else {
                setTextAppearance(R.style.ChipText);//Crash sur Android 5
            }
        }
    }

    factory<FrameLayout> { (activity: Activity, view: View) ->

        FrameLayout(activity).apply {
            id = View.NO_ID
            addView(view)

            val marginSizeInDP = resources.getDimensionPixelSize(R.dimen.standard_margin)

            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(marginSizeInDP, marginSizeInDP, marginSizeInDP, marginSizeInDP)
            view.layoutParams = params
        }
    }

    factory<TextView> { (activity: Activity, message: String) ->

        TextView(activity).apply {
            id = View.NO_ID
            text = message
            setTextColorFromAttrRes(android.R.attr.textColorSecondary)

            val textSizeInDP = resources.getDimension(R.dimen.standard_text_size) / resources.displayMetrics.density
            textSize = textSizeInDP
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium_italic)
        }
    }

    factory<AlertDialog>(named(DIALOG_SIMPLE_VIEW_KOIN_NAMED)) { (activity: Activity, title: String, message: String) ->

        val textView = get<TextView> { parametersOf(activity, message) }
        val frameLayout = get<FrameLayout> { parametersOf(activity, textView) }

        AlertDialog.Builder(activity).apply {
            setTitle(title)
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
            setView(frameLayout)
        }.create().apply {
            setOnShowListener {
                getButton(AlertDialog.BUTTON_NEGATIVE).setTextColorFromAttrRes(R.attr.colorAccent)
                getButton(AlertDialog.BUTTON_POSITIVE).setTextColorFromAttrRes(R.attr.colorAccent)
                getButton(AlertDialog.BUTTON_NEUTRAL).setTextColorFromAttrRes(R.attr.colorAccent)
            }
        }
    }
}

val scopesModule: Module = module {

    scope(named(BARCODE_ANALYSIS_SCOPE_SESSION)) {

        scoped { (contents: String, format: BarcodeFormat) ->
            val result = Result(contents, null, null, format)
            ResultParser.parseResult(result)
        }

        scoped<BarcodeType> { (contents: String, format: BarcodeFormat) ->

            val parsedResult: ParsedResult = this@scoped.get { parametersOf(contents, format) }

            when(parsedResult.type){
                ParsedResultType.ADDRESSBOOK -> BarcodeType.CONTACT
                ParsedResultType.EMAIL_ADDRESS -> BarcodeType.MAIL
                ParsedResultType.PRODUCT -> BarcodeType.UNKNOWN_PRODUCT
                ParsedResultType.URI -> BarcodeType.URL
                ParsedResultType.TEXT -> BarcodeType.TEXT
                ParsedResultType.GEO -> BarcodeType.LOCALISATION
                ParsedResultType.TEL -> BarcodeType.PHONE
                ParsedResultType.SMS -> BarcodeType.SMS
                ParsedResultType.CALENDAR -> BarcodeType.AGENDA
                ParsedResultType.WIFI -> BarcodeType.WIFI
                ParsedResultType.ISBN -> BarcodeType.BOOK
                ParsedResultType.VIN -> BarcodeType.TEXT
                else -> BarcodeType.UNKNOWN
            }
        }
    }

    // Scope utilisé par les ActionFragment
    scope(named(ACTION_SCOPE_SESSION)) {

        // ---- Wi-Fi ----
        scoped<WifiSetupData> { (parsedResult: WifiParsedResult) ->

            WifiSetupData(
                authType = parsedResult.networkEncryption ?: "",
                name = parsedResult.ssid ?: "",
                password = parsedResult.password ?: "",
                isHidden = parsedResult.isHidden,
                anonymousIdentity = "",
                identity = "",
                eapMethod = "",
                phase2Method = ""
            )
        }

        /* -----------------------------------------------------------------------------------------
        ---------------------------- Intent --------------------------------------------------------
        ----------------------------------------------------------------------------------------- */

        // ---- Agenda ----
        scoped<Intent>(named(ActionEnum.ADD_AGENDA)) { (parsedResult: CalendarParsedResult) ->
            get(named(INTENT_ADD_AGENDA)) { parametersOf(parsedResult) }
        }

        // ------------------------------------ ADD INTO CONTACT -----------------------------------

        // ---- Add VCard Into Contact ----
        scoped<Intent>(named(ActionEnum.ADD_CONTACT)) { (parsedResult: AddressBookParsedResult) ->
            get(named(INTENT_ADD_CONTACT)) { parametersOf(parsedResult) }
        }

        // ---- Add Email Into Contact ----
        scoped<Intent>(named(ActionEnum.ADD_MAIL)) { (parsedResult: EmailAddressParsedResult) ->
            get(named(INTENT_ADD_EMAIL)) { parametersOf(parsedResult) }
        }

        // ---- Add Phone Into Contact from TelParsedResult ----
        scoped<Intent>(named(ActionEnum.ADD_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
            get(named(INTENT_ADD_PHONE_NUMBER)) { parametersOf(parsedResult) }
        }

        // ---- Add Phone Into Contact from SMSParsedResult ----
        scoped<Intent>(named(ActionEnum.ADD_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->
            get(named(INTENT_ADD_SMS_NUMBER)) { parametersOf(parsedResult) }
        }

        // -----------------------------------------------------------------------------------------

        // ---- Send Email ----
        scoped<Intent>(named(ActionEnum.SEND_MAIL)) { (parsedResult: EmailAddressParsedResult) ->
            get(named(INTENT_SEND_EMAIL)) { parametersOf(parsedResult) }
        }

        // --------------------------------- CALL --------------------------------------------------

        // ---- Call Phone ----
        scoped<Intent>(named(ActionEnum.CALL_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
            get(named(INTENT_CALL_PHONE_NUMBER)) { parametersOf(parsedResult) }
        }

        // ---- Call from SMSParsedResult ----
        scoped<Intent>(named(ActionEnum.CALL_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->
            get(named(INTENT_CALL_SMS_NUMBER)) { parametersOf(parsedResult) }
        }

        // --------------------------------- SEND SMS ----------------------------------------------

        // ---- Send SMS from TelParsedResult ----
        scoped<Intent>(named(ActionEnum.SEND_SMS_TO_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
            get(named(INTENT_SEND_SMS_TO_PHONE_NUMBER)) { parametersOf(parsedResult) }
        }

        // ---- Send SMS ----
        scoped<Intent>(named(ActionEnum.SEND_SMS)) { (parsedResult: SMSParsedResult) ->
            get(named(INTENT_SEND_SMS_TO_SMS_NUMBER)) { parametersOf(parsedResult) }
        }

        // -----------------------------------------------------------------------------------------

        // ---- Localisation ----
        scoped<Intent>(named(ActionEnum.SEARCH_LOCALISATION)) { (parsedResult: GeoParsedResult) ->
            //get(named(INTENT_SEARCH_LOCALISATION)) { parametersOf(parsedResult) }
            get(named(INTENT_SEARCH_URL)) { parametersOf(parsedResult.geoURI) }
        }

        // ---- URL ----
        scoped<Intent>(named(ActionEnum.OPEN_IN_WEB_BROWSER)) { (parsedResult: URIParsedResult) ->
            //get(named(INTENT_SEARCH_URL)) { parametersOf(parsedResult) }
            get(named(INTENT_SEARCH_URL)) { parametersOf(parsedResult.uri) }
        }

        scoped<Intent>(named(ActionEnum.SEARCH_WITH_ENGINE)) { (url: String) ->
            get(named(INTENT_SEARCH_URL)) { parametersOf(url) }
        }
    }
}