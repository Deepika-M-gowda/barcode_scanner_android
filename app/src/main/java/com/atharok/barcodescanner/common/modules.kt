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

package com.atharok.barcodescanner.common

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extentions.setTextColorFromAttrRes
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
import com.atharok.barcodescanner.presentation.views.fragments.barcodeCreatorForms.forms.*
import com.google.android.material.chip.Chip
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.Result
import com.google.zxing.client.android.BeepManager
import com.google.zxing.client.result.*
import com.journeyapps.barcodescanner.BarcodeEncoder
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.*
import kotlin.reflect.KClass

val appModules by lazy {
    listOf<Module>(
        androidModule,
        libraryModule,
        libraryApi29Module,
        viewModelModule,
        useCaseModule,
        repositoryModule,
        dataModule,
        scopesModule,
        viewsModule
    )
}

val androidModule: Module = module {
    single<Vibrator> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = androidApplication().applicationContext.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            androidApplication().applicationContext.getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
    }
    single<ConnectivityManager> { androidApplication().applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }
    single<ClipboardManager> { androidApplication().applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }
    single<InputMethodManager> { androidApplication().applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager }
    single<WifiManager> { androidApplication().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    factory<Barcode> { (contents: String, formatName: String) ->
        Barcode(contents, formatName, System.currentTimeMillis())
    }

    // ---- Share Text ----
    factory<Intent>(named(ActionEnum.SHARE_TEXT)) { (contents: String) ->

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, contents)
        }

        Intent.createChooser(intent, androidContext().getString(R.string.intent_chooser_share_title))
    }

    // ---- Share Image ----
    factory<Intent>(named(ActionEnum.SHARE_IMAGE)) { (uri: Uri) ->

        val intent: Intent = Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // -> For call startActivity() from outside of an Activity context
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            putExtra(Intent.EXTRA_STREAM, uri)
        }

        val chooser = Intent.createChooser(intent, androidContext().getString(R.string.intent_chooser_share_title))

        val resInfoList: List<ResolveInfo> = androidContext().packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            androidContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        chooser
    }
}

val libraryModule: Module = module {
    single<SettingsManager> { SettingsManager(androidContext()) }
    single<BitmapBarcodeAnalyser>{ BitmapBarcodeAnalyser() }
    single<BarcodeFormatChecker> { BarcodeFormatChecker() }
    single<VCardReader> { VCardReader(androidContext()) }
    single<MultiFormatWriter> { MultiFormatWriter() }
    single<BarcodeEncoder> { BarcodeEncoder() }
    single<BitmapBarcodeGenerator> { BitmapBarcodeGenerator(get<MultiFormatWriter>(), get<BarcodeEncoder>()) }
    single<BitmapRecorder> { BitmapRecorder(androidContext()) }
    single<BitmapSharer> { BitmapSharer(androidContext()) }
    single<WifiSetupWithOldLibrary> { WifiSetupWithOldLibrary() }

    factory<BeepManager> { (activity: Activity) ->
        val settingsManager = get<SettingsManager>()
        BeepManager(activity).apply {
            isBeepEnabled = settingsManager.useBipScan
            isVibrateEnabled = settingsManager.useVibrateScan
        }
    }
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

val viewsModule = module {

    factory<Chip> { (activity: Activity, text: String) ->

        Chip(activity).apply {
            id = View.NO_ID
            this.text = text
            setEnsureMinTouchTargetSize(false)
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            setTextAppearance(R.style.ChipText)
        }
    }

    factory<FrameLayout> {
            (activity: Activity, view: View) ->

        FrameLayout(activity).apply {
            id = View.NO_ID
            addView(view)

            val marginSizeInDP = resources.getDimensionPixelSize(R.dimen.standard_margin)

            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(marginSizeInDP, marginSizeInDP, marginSizeInDP, marginSizeInDP)
            view.layoutParams = params
        }
    }

    factory<TextView> {
            (activity: Activity, message: String) ->

        TextView(activity).apply {
            id = View.NO_ID
            text = message
            setTextColorFromAttrRes(android.R.attr.textColorSecondary)

            val textSizeInDP = resources.getDimension(R.dimen.standard_text_size) / resources.displayMetrics.density
            textSize = textSizeInDP
            typeface = ResourcesCompat.getFont(context, R.font.roboto_medium_italic)
        }
    }

    factory<AlertDialog>(named(DIALOG_SIMPLE_VIEW_KOIN_NAMED)) {
            (activity: Activity, title: String, message: String) ->

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

    // Scope utilisé par BarcodeAnalysisActivity et ses sous fragments
    scope(named(BARCODE_ANALYSIS_SCOPE_SESSION)) {

        scoped<ParsedResult> { (contents: String, format: BarcodeFormat) ->
            val result = Result(contents, null, null, format)
            ResultParser.parseResult(result)
        }

        scoped<BarcodeType> { (contents: String, format: BarcodeFormat) ->

            val parsedResult = get<ParsedResult>{
                parametersOf(contents, format)
            }

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

        scoped<KClass<out ActionsFragment>> { (barcodeType: BarcodeType) ->

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

            Intent(Intent.ACTION_EDIT).apply {
                type = "vnd.android.cursor.item/event"
                putExtra(CalendarContract.Events.ALL_DAY, parsedResult.isStartAllDay && parsedResult.isEndAllDay)
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, parsedResult.startTimestamp + TimeZone.getTimeZone("GMT").rawOffset - TimeZone.getDefault().rawOffset)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, parsedResult.endTimestamp + TimeZone.getTimeZone("GMT").rawOffset - TimeZone.getDefault().rawOffset)
                putExtra(CalendarContract.Events.TITLE, parsedResult.summary ?: "")
                putExtra(CalendarContract.Events.EVENT_LOCATION, parsedResult.location ?: "")
                putExtra(CalendarContract.Events.DESCRIPTION, parsedResult.description ?: "")
                putExtra(CalendarContract.Events.ORGANIZER, parsedResult.organizer ?: "")
            }
        }

        // ------------------------------------ ADD INTO CONTACT -----------------------------------

        // ---- Add VCard Into Contact ----
        scoped<Intent>(named(ActionEnum.ADD_CONTACT)) { (parsedResult: AddressBookParsedResult) ->

            Intent(ContactsContract.Intents.Insert.ACTION).apply {

                type = ContactsContract.RawContacts.CONTENT_TYPE

                if(parsedResult.names?.isNotEmpty() == true)
                    putExtra(ContactsContract.Intents.Insert.NAME, parsedResult.names?.get(0) ?: "")

                putExtra(ContactsContract.Intents.Insert.COMPANY, parsedResult.org ?: "")
                putExtra(ContactsContract.Intents.Insert.JOB_TITLE, parsedResult.title ?: "")

                if(parsedResult.addresses?.isNotEmpty() == true)
                    putExtra(ContactsContract.Intents.Insert.POSTAL, parsedResult.addresses?.get(0) ?: "")

                if(parsedResult.phoneNumbers != null) {
                    if (parsedResult.phoneNumbers.isNotEmpty())
                        putExtra(ContactsContract.Intents.Insert.PHONE, parsedResult.phoneNumbers?.get(0) ?: "")

                    if (parsedResult.phoneNumbers.size > 1)
                        putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE, parsedResult.phoneNumbers?.get(1) ?: "")

                    if (parsedResult.phoneNumbers.size > 2)
                        putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE, parsedResult.phoneNumbers?.get(2) ?: "")
                }

                if(parsedResult.phoneTypes != null) {
                    if(parsedResult.phoneTypes.isNotEmpty())
                        putExtra(ContactsContract.Intents.Insert.PHONE_TYPE, parsedResult.phoneTypes?.get(0) ?: "")

                    if(parsedResult.phoneTypes.size > 1)
                        putExtra(ContactsContract.Intents.Insert.SECONDARY_PHONE_TYPE, parsedResult.phoneTypes?.get(1) ?: "")

                    if(parsedResult.phoneTypes.size > 2)
                        putExtra(ContactsContract.Intents.Insert.TERTIARY_PHONE_TYPE, parsedResult.phoneTypes?.get(2) ?: "")
                }

                if(parsedResult.emails != null) {
                    if (parsedResult.emails.isNotEmpty())
                        putExtra(ContactsContract.Intents.Insert.EMAIL, parsedResult.emails?.get(0) ?: "")

                    if (parsedResult.emails.size > 1)
                        putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL, parsedResult.emails?.get(1) ?: "")

                    if (parsedResult.emails.size > 2)
                        putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL, parsedResult.emails?.get(2) ?: "")
                }

                if(parsedResult.emailTypes != null) {
                    if (parsedResult.emailTypes.isNotEmpty())
                        putExtra(ContactsContract.Intents.Insert.EMAIL_TYPE, parsedResult.emailTypes?.get(0) ?: "")

                    if (parsedResult.emailTypes.size > 1)
                        putExtra(ContactsContract.Intents.Insert.SECONDARY_EMAIL_TYPE, parsedResult.emailTypes?.get(1) ?: "")

                    if (parsedResult.emailTypes.size > 2)
                        putExtra(ContactsContract.Intents.Insert.TERTIARY_EMAIL_TYPE, parsedResult.emailTypes?.get(2) ?: "")
                }

                putExtra(ContactsContract.Intents.Insert.NOTES, parsedResult.note ?: "")
            }
        }

        // ---- Add Email Into Contact ----
        scoped<Intent>(named(ActionEnum.ADD_MAIL)) { (parsedResult: EmailAddressParsedResult) ->

            val email: String = if(parsedResult.tos?.isNotEmpty() == true) parsedResult.tos.first() else ""

            Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.EMAIL, email)
            }
        }

        // ---- Add Phone Into Contact from TelParsedResult ----
        scoped<Intent>(named(ActionEnum.ADD_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->

            Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                putExtra(ContactsContract.Intents.Insert.PHONE, parsedResult.number)
            }
        }

        // ---- Add Phone Into Contact from SMSParsedResult ----
        scoped<Intent>(named(ActionEnum.ADD_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->

            Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
                if(parsedResult.numbers?.isNotEmpty() == true) putExtra(
                    ContactsContract.Intents.Insert.PHONE,
                    parsedResult.numbers.first()
                )
            }
        }

        // -----------------------------------------------------------------------------------------

        // ---- Send Email ----
        scoped<Intent>(named(ActionEnum.SEND_MAIL)) { (parsedResult: EmailAddressParsedResult) ->

            val email: String = if(parsedResult.tos?.isNotEmpty() == true) parsedResult.tos.first() else ""
            val uri = Uri.parse("mailto:$email")

            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                putExtra(Intent.EXTRA_SUBJECT, parsedResult.subject ?: "")
                putExtra(Intent.EXTRA_TEXT, parsedResult.body ?: "")
            }

            Intent.createChooser(intent, androidContext().getString(R.string.intent_chooser_mail_title))
        }

        // --------------------------------- CALL --------------------------------------------------

        // ---- Call Phone ----
        scoped<Intent>(named(ActionEnum.CALL_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
            Intent(Intent.ACTION_DIAL, Uri.parse(parsedResult.telURI))
        }

        // ---- Call from SMSParsedResult ----
        scoped<Intent>(named(ActionEnum.CALL_SMS_NUMBER)) { (parsedResult: SMSParsedResult) ->

            val phone = if(parsedResult.numbers?.isNotEmpty() == true) parsedResult.numbers.first() else ""

            Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
        }

        // --------------------------------- SEND SMS ----------------------------------------------

        // ---- Send SMS from TelParsedResult ----
        scoped<Intent>(named(ActionEnum.SEND_SMS_TO_PHONE_NUMBER)) { (parsedResult: TelParsedResult) ->
            Intent(Intent.ACTION_VIEW, Uri.parse("smsto:${parsedResult.number}"))
        }

        // ---- Send SMS ----
        scoped<Intent>(named(ActionEnum.SEND_SMS)) { (parsedResult: SMSParsedResult) ->
            Intent(Intent.ACTION_VIEW, Uri.parse(parsedResult.smsuri))
        }

        // -----------------------------------------------------------------------------------------

        // ---- Localisation ----
        scoped<Intent>(named(ActionEnum.SEARCH_LOCALISATION)) { (parsedResult: GeoParsedResult) ->
            Intent(Intent.ACTION_VIEW, Uri.parse(parsedResult.geoURI))
        }

        // ---- URL ----
        scoped<Intent>(named(ActionEnum.SEARCH_URL)) { (parsedResult: URIParsedResult) ->
            Intent(Intent.ACTION_VIEW, Uri.parse(parsedResult.uri))
        }

        scoped<Intent>(named(ActionEnum.SEARCH_WITH_ENGINE)) { (url: String) ->
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }

    // Scope utilisé par les FormCreateBarcodeActivity
    scope(named(BARCODE_CREATOR_SCOPE_SESSION)) {

        scoped<AbstractFormCreateBarcodeFragment> {
                (type: BarcodeFormatDetails) ->

            when(type){
                BarcodeFormatDetails.QR_TEXT -> FormCreateQrCodeTextFragment()
                BarcodeFormatDetails.QR_URL -> FormCreateQrCodeUrlFragment()
                BarcodeFormatDetails.QR_CONTACT -> FormCreateQrCodeContactFragment()
                BarcodeFormatDetails.QR_MAIL -> FormCreateQrCodeMailFragment()
                BarcodeFormatDetails.QR_SMS -> FormCreateQrCodeSmsFragment()
                BarcodeFormatDetails.QR_PHONE -> FormCreateQrCodePhoneFragment()
                BarcodeFormatDetails.QR_LOCALISATION -> FormCreateQrCodeLocalisationFragment()
                BarcodeFormatDetails.QR_AGENDA -> FormCreateQrCodeAgendaFragment()
                BarcodeFormatDetails.QR_WIFI -> FormCreateQrCodeWifiFragment()
                else -> {

                    val bundle = Bundle().apply {
                        putSerializable(BARCODE_FORMAT_KEY, type.format)
                    }

                    FormCreateBarcodeFragment().apply {
                        arguments = bundle
                    }
                }
            }
        }
    }
}