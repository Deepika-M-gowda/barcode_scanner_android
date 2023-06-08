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
import android.graphics.Typeface
import android.location.LocationManager
import android.net.ConnectivityManager
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
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.setTextIsSelectableCompat
import com.atharok.barcodescanner.common.utils.*
import com.atharok.barcodescanner.data.api.CoverArtArchiveService
import com.atharok.barcodescanner.data.api.MusicBrainzService
import com.atharok.barcodescanner.data.api.OpenBeautyFactsService
import com.atharok.barcodescanner.data.api.OpenFoodFactsService
import com.atharok.barcodescanner.data.api.OpenLibraryService
import com.atharok.barcodescanner.data.api.OpenPetFoodFactsService
import com.atharok.barcodescanner.data.database.AppDatabase
import com.atharok.barcodescanner.data.database.BankDao
import com.atharok.barcodescanner.data.database.BarcodeDao
import com.atharok.barcodescanner.data.database.createBankDao
import com.atharok.barcodescanner.data.database.createBarcodeDao
import com.atharok.barcodescanner.data.database.createDatabase
import com.atharok.barcodescanner.data.file.FileFetcher
import com.atharok.barcodescanner.data.network.createApiClient
import com.atharok.barcodescanner.data.repositories.*
import com.atharok.barcodescanner.domain.entity.barcode.Barcode
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeFormatDetails
import com.atharok.barcodescanner.domain.entity.barcode.BarcodeType
import com.atharok.barcodescanner.domain.entity.barcode.QrCodeErrorCorrectionLevel
import com.atharok.barcodescanner.domain.library.*
import com.atharok.barcodescanner.domain.library.wifiSetup.WifiConnect
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithNewLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.configuration.WifiSetupWithOldLibrary
import com.atharok.barcodescanner.domain.library.wifiSetup.data.WifiSetupData
import com.atharok.barcodescanner.domain.repositories.*
import com.atharok.barcodescanner.domain.usecases.DatabaseBankUseCase
import com.atharok.barcodescanner.domain.usecases.DatabaseBarcodeUseCase
import com.atharok.barcodescanner.domain.usecases.ExternalFoodProductDependencyUseCase
import com.atharok.barcodescanner.domain.usecases.ProductUseCase
import com.atharok.barcodescanner.presentation.intent.*
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBankViewModel
import com.atharok.barcodescanner.presentation.viewmodel.DatabaseBarcodeViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ExternalFileViewModel
import com.atharok.barcodescanner.presentation.viewmodel.InstalledAppsViewModel
import com.atharok.barcodescanner.presentation.viewmodel.ProductViewModel
import com.atharok.barcodescanner.presentation.views.fragments.barcodeAnalysis.actions.*
import com.atharok.barcodescanner.presentation.views.fragments.barcodeFormCreator.*
import com.atharok.barcodescanner.presentation.views.fragments.main.*
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.Result
import com.google.zxing.ResultMetadataType
import com.google.zxing.client.result.*
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
    single<LocationManager> { androidApplication().applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    factory<Barcode> { (contents: String, formatName: String, qrErrorCorrectionLevel: QrCodeErrorCorrectionLevel) ->
        Barcode(contents, formatName, System.currentTimeMillis(), errorCorrectionLevel = qrErrorCorrectionLevel.name)
    }

    factory<QrCodeErrorCorrectionLevel>(named(KOIN_NAMED_ERROR_CORRECTION_LEVEL_BY_STRING)) { (errorCorrectionLevel: String?) ->
        when(errorCorrectionLevel){
            "L" -> QrCodeErrorCorrectionLevel.L
            "M" -> QrCodeErrorCorrectionLevel.M
            "Q" -> QrCodeErrorCorrectionLevel.Q
            "H" -> QrCodeErrorCorrectionLevel.H
            else -> QrCodeErrorCorrectionLevel.NONE
        }
    }

    factory<QrCodeErrorCorrectionLevel>(named(KOIN_NAMED_ERROR_CORRECTION_LEVEL_BY_RESULT)) { (result: Result) ->
        var errorCorrectionLevel: QrCodeErrorCorrectionLevel = QrCodeErrorCorrectionLevel.NONE
        result.resultMetadata?.let { metadata ->
            val errorCorrectionLevelStr = metadata[ResultMetadataType.ERROR_CORRECTION_LEVEL] as? String
            errorCorrectionLevel = get(named(KOIN_NAMED_ERROR_CORRECTION_LEVEL_BY_STRING)) {
                parametersOf(errorCorrectionLevelStr)
            }
        }
        errorCorrectionLevel
    }

    factory { Bundle() }
}

val libraryModule: Module = module {
    single<SettingsManager> { SettingsManager(androidContext()) }
    single<BitmapBarcodeAnalyser>{ BitmapBarcodeAnalyser() }
    single<BarcodeFormatChecker> { BarcodeFormatChecker() }
    single<VCardReader> { VCardReader(androidContext()) }
    single<MultiFormatWriter> { MultiFormatWriter() }
    single<BitmapBarcodeGenerator> { BitmapBarcodeGenerator(get<MultiFormatWriter>()) }
    single<BitmapRecorder> { BitmapRecorder(androidContext()) }
    single<BitmapSharer> { BitmapSharer(androidContext()) }
    single<WifiSetupWithOldLibrary> { WifiSetupWithOldLibrary() }
    single<Iban> { Iban() }
    single<InternetChecker> { InternetChecker() }
    single<DateConverter> { DateConverter() }

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
        DatabaseBankViewModel(get<DatabaseBankUseCase>())
    }

    viewModel {
        DatabaseBarcodeViewModel(get<DatabaseBarcodeUseCase>())
    }

    viewModel {
        ExternalFileViewModel(get<ExternalFoodProductDependencyUseCase>())
    }

    viewModel {
        InstalledAppsViewModel(get<InstalledAppsRepository>())
    }
}

val useCaseModule: Module = module {
    single<ProductUseCase> {
        ProductUseCase(
            foodProductRepository = get<FoodProductRepository>(),
            beautyProductRepository = get<BeautyProductRepository>(),
            petFoodProductRepository = get<PetFoodProductRepository>(),
            musicProductRepository = get<MusicProductRepository>(),
            bookProductRepository = get<BookProductRepository>()
        )
    }

    single<DatabaseBankUseCase> {
        DatabaseBankUseCase(get<BankRepository>())
    }

    single<DatabaseBarcodeUseCase> {
        DatabaseBarcodeUseCase(get<BarcodeRepository>())
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

    single<MusicProductRepository> {
        MusicProductRepositoryImpl(get<MusicBrainzService>(), get<CoverArtArchiveService>())
    }

    single<BookProductRepository> {
        BookProductRepositoryImpl(get<OpenLibraryService>())
    }

    single<BankRepository> {
        BankRepositoryImpl(get<BankDao>())
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

    single<InstalledAppsRepository> {
        InstalledAppsRepositoryImpl(androidContext())
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

    single<MusicBrainzService> {
        val baseUrl = androidContext().getString(R.string.base_api_musicbrainz_url)
        createApiClient(androidContext(), baseUrl).create(MusicBrainzService::class.java)
    }

    single<CoverArtArchiveService> {
        val baseUrl = androidContext().getString(R.string.base_api_cover_art_archive_url)
        createApiClient(androidContext(), baseUrl).create(CoverArtArchiveService::class.java)
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

    single<BankDao> {
        createBankDao(get<AppDatabase>())
    }

    single<FileFetcher> { FileFetcher(androidContext()) }
}

val fragmentsModule = module {

    factory { MainCameraXScannerFragment() }
    factory { MainScannerFragment() }
    factory { MainBarcodeHistoryFragment() }
    factory { MainBarcodeCreatorListFragment() }
    factory { MainSettingsFragment() }

    factory { BarcodeFormCreatorAztecFragment() }
    factory { BarcodeFormCreatorCodabarFragment() }
    factory { BarcodeFormCreatorCode39Fragment() }
    factory { BarcodeFormCreatorCode93Fragment() }
    factory { BarcodeFormCreatorCode128Fragment() }
    factory { BarcodeFormCreatorDataMatrixFragment() }
    factory { BarcodeFormCreatorEAN8Fragment() }
    factory { BarcodeFormCreatorEAN13Fragment() }
    factory { BarcodeFormCreatorITFFragment() }
    factory { BarcodeFormCreatorPDF417Fragment() }
    factory { BarcodeFormCreatorQrAgendaFragment() }
    factory { BarcodeFormCreatorQrApplicationFragment() }
    factory { BarcodeFormCreatorQrContactFragment() }
    factory { BarcodeFormCreatorQrEpcFragment() }
    factory { BarcodeFormCreatorQrLocalisationFragment() }
    factory { BarcodeFormCreatorQrMailFragment() }
    factory { BarcodeFormCreatorQrPhoneFragment() }
    factory { BarcodeFormCreatorQrSmsFragment() }
    factory { BarcodeFormCreatorQrTextFragment() }
    factory { BarcodeFormCreatorQrUrlFragment() }
    factory { BarcodeFormCreatorQrWifiFragment() }
    factory { BarcodeFormCreatorUPCAFragment() }
    factory { BarcodeFormCreatorUPCEFragment() }

    factory<AbstractBarcodeFormCreatorFragment> { (barcodeFormatDetails: BarcodeFormatDetails) ->
        when(barcodeFormatDetails){
            BarcodeFormatDetails.AZTEC -> get<BarcodeFormCreatorAztecFragment>()
            BarcodeFormatDetails.CODABAR -> get<BarcodeFormCreatorCodabarFragment>()
            BarcodeFormatDetails.CODE_39 -> get<BarcodeFormCreatorCode39Fragment>()
            BarcodeFormatDetails.CODE_93 -> get<BarcodeFormCreatorCode93Fragment>()
            BarcodeFormatDetails.CODE_128 -> get<BarcodeFormCreatorCode128Fragment>()
            BarcodeFormatDetails.DATA_MATRIX -> get<BarcodeFormCreatorDataMatrixFragment>()
            BarcodeFormatDetails.EAN_8 -> get<BarcodeFormCreatorEAN8Fragment>()
            BarcodeFormatDetails.EAN_13 -> get<BarcodeFormCreatorEAN13Fragment>()
            BarcodeFormatDetails.ITF -> get<BarcodeFormCreatorITFFragment>()
            BarcodeFormatDetails.PDF_417 -> get<BarcodeFormCreatorPDF417Fragment>()
            BarcodeFormatDetails.QR_AGENDA -> get<BarcodeFormCreatorQrAgendaFragment>()
            BarcodeFormatDetails.QR_APPLICATION -> get<BarcodeFormCreatorQrApplicationFragment>()
            BarcodeFormatDetails.QR_CONTACT -> get<BarcodeFormCreatorQrContactFragment>()
            BarcodeFormatDetails.QR_EPC -> get<BarcodeFormCreatorQrEpcFragment>()
            BarcodeFormatDetails.QR_LOCALISATION -> get<BarcodeFormCreatorQrLocalisationFragment>()
            BarcodeFormatDetails.QR_MAIL -> get<BarcodeFormCreatorQrMailFragment>()
            BarcodeFormatDetails.QR_PHONE -> get<BarcodeFormCreatorQrPhoneFragment>()
            BarcodeFormatDetails.QR_SMS -> get<BarcodeFormCreatorQrSmsFragment>()
            BarcodeFormatDetails.QR_TEXT -> get<BarcodeFormCreatorQrTextFragment>()
            BarcodeFormatDetails.QR_URL -> get<BarcodeFormCreatorQrUrlFragment>()
            BarcodeFormatDetails.QR_WIFI -> get<BarcodeFormCreatorQrWifiFragment>()
            BarcodeFormatDetails.UPC_A -> get<BarcodeFormCreatorUPCAFragment>()
            BarcodeFormatDetails.UPC_E -> get<BarcodeFormCreatorUPCEFragment>()
        }
    }

    factory<KClass<out AbstractActionsFragment>> { (barcodeType: BarcodeType) ->
        when(barcodeType){
            BarcodeType.AGENDA -> AgendaActionsFragment::class
            BarcodeType.CONTACT -> ContactActionsFragment::class
            BarcodeType.LOCALISATION -> LocalizationActionsFragment::class
            BarcodeType.MAIL -> EmailActionsFragment::class
            BarcodeType.PHONE -> PhoneActionsFragment::class
            BarcodeType.SMS -> SmsActionsFragment::class
            BarcodeType.TEXT -> DefaultActionsFragment::class
            BarcodeType.URL -> UrlActionsFragment::class
            BarcodeType.WIFI -> WifiActionsFragment::class
            BarcodeType.FOOD -> FoodActionsFragment::class
            BarcodeType.PET_FOOD -> PetFoodActionsFragment::class
            BarcodeType.BEAUTY -> BeautyActionsFragment::class
            BarcodeType.MUSIC -> MusicActionsFragment::class
            BarcodeType.BOOK -> BookActionsFragment::class
            BarcodeType.INDUSTRIAL -> DefaultActionsFragment::class
            BarcodeType.MATRIX -> DefaultActionsFragment::class
            BarcodeType.UNKNOWN -> DefaultActionsFragment::class
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
            //setTextColorFromAttrRes(android.R.attr.textColorSecondary)

            val textSizeInDP = resources.getDimension(R.dimen.standard_text_size) / resources.displayMetrics.density
            textSize = textSizeInDP
            typeface = Typeface.create("sans-serif-medium", Typeface.ITALIC)//ResourcesCompat.getFont(context, R.font.roboto_medium_italic)
            setTextIsSelectableCompat(true)
        }
    }

    factory<AlertDialog> { (activity: Activity, title: String, message: String) ->

        val textView = get<TextView> { parametersOf(activity, message) }
        val frameLayout = get<FrameLayout> { parametersOf(activity, textView) }

        MaterialAlertDialogBuilder(activity, R.style.AppTheme_MaterialAlertDialog).apply {
            setTitle(title)
            setNegativeButton(R.string.close_dialog_label) {
                    dialogInterface, _ -> dialogInterface.cancel()
            }
            setView(frameLayout)
        }.create()
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

        scoped<ParsedResult> { (barcode: Barcode) ->
            val result = Result(barcode.contents, null, null, barcode.getBarcodeFormat())
            ResultParser.parseResult(result)
        }

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

        scoped { WifiConnect() }
    }
}