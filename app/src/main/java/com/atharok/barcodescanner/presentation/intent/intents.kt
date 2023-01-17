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

package com.atharok.barcodescanner.presentation.intent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.*
import androidx.annotation.RequiresApi
import com.atharok.barcodescanner.R
import com.atharok.barcodescanner.common.extensions.queryIntentActivitiesAppCompat
import com.google.zxing.client.result.*
import java.util.*
import kotlin.reflect.KClass

fun createStartActivityIntent(context: Context, kClass: KClass<*>): Intent {
    return Intent(context, kClass.java)
}

fun createPickImageIntent(): Intent = Intent(Intent.ACTION_PICK).apply {
    type = "image/*"
}

fun createPickContactIntent(): Intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI).apply {
    type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
}

fun createPickWifiNetworkIntent(): Intent = Intent(WifiManager.ACTION_PICK_WIFI_NETWORK)

@RequiresApi(Build.VERSION_CODES.R)
fun createWifiAddNetworksIntent(): Intent = Intent(Settings.ACTION_WIFI_ADD_NETWORKS)

fun createActionCreateImageIntent(name: String): Intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
    addCategory(Intent.CATEGORY_OPENABLE)
    type = "image/png"

    putExtra(Intent.EXTRA_TITLE, name)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        putExtra(DocumentsContract.EXTRA_INITIAL_URI, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    }
}

fun createShareTextIntent(context: Context, text: String): Intent {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }

    return Intent.createChooser(intent, context.getString(R.string.intent_chooser_share_title))
}

fun createShareImageIntent(context: Context, uri: Uri): Intent {
    val intent: Intent = Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // -> For call startActivity() from outside of an Activity context
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        putExtra(Intent.EXTRA_STREAM, uri)
    }

    val chooser = Intent.createChooser(intent, context.getString(R.string.intent_chooser_share_title))

    val resInfoList: List<ResolveInfo> =
        context.packageManager.queryIntentActivitiesAppCompat(chooser, PackageManager.MATCH_DEFAULT_ONLY)

    for (resolveInfo in resInfoList) {
        val packageName = resolveInfo.activityInfo.packageName
        context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    return chooser
}

fun createAddAgendaIntent(parsedResult: CalendarParsedResult): Intent = Intent(Intent.ACTION_EDIT).apply {
    type = "vnd.android.cursor.item/event"
    putExtra(CalendarContract.Events.ALL_DAY, parsedResult.isStartAllDay && parsedResult.isEndAllDay)
    putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, parsedResult.startTimestamp + TimeZone.getTimeZone("GMT").rawOffset - TimeZone.getDefault().rawOffset)
    putExtra(CalendarContract.EXTRA_EVENT_END_TIME, parsedResult.endTimestamp + TimeZone.getTimeZone("GMT").rawOffset - TimeZone.getDefault().rawOffset)
    putExtra(CalendarContract.Events.TITLE, parsedResult.summary ?: "")
    putExtra(CalendarContract.Events.EVENT_LOCATION, parsedResult.location ?: "")
    putExtra(CalendarContract.Events.DESCRIPTION, parsedResult.description ?: "")
    putExtra(CalendarContract.Events.ORGANIZER, parsedResult.organizer ?: "")
}

fun createAddContactIntent(parsedResult: AddressBookParsedResult): Intent {
    return Intent(ContactsContract.Intents.Insert.ACTION).apply {

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

// ------------------------------------------ ADD CONTACT ------------------------------------------

private fun createAddValueIntoContactIntent(name: String, value: String?): Intent {
    return Intent(ContactsContract.Intents.Insert.ACTION).apply {
        type = ContactsContract.RawContacts.CONTENT_TYPE
        if(!value.isNullOrBlank())
            putExtra(name, value)
    }
}

fun createAddEmailIntent(parsedResult: EmailAddressParsedResult): Intent {
    val email: String? = if(parsedResult.tos?.isNotEmpty() == true) parsedResult.tos.first() else null
    return createAddValueIntoContactIntent(ContactsContract.Intents.Insert.EMAIL, email)
}

fun createAddPhoneNumberIntent(parsedResult: TelParsedResult): Intent {
    return createAddValueIntoContactIntent(ContactsContract.Intents.Insert.PHONE, parsedResult.number)
}

fun createAddSmsNumberIntent(parsedResult: SMSParsedResult): Intent {
    val number: String? = if(parsedResult.numbers?.isNotEmpty() == true) parsedResult.numbers.first() else null
    return createAddValueIntoContactIntent(ContactsContract.Intents.Insert.PHONE, number)
}

// ------------------------------------------ SEND EMAIL -------------------------------------------

fun createSendEmailIntent(context: Context, parsedResult: EmailAddressParsedResult): Intent {

    val email: String = if(parsedResult.tos?.isNotEmpty() == true) parsedResult.tos.first() else ""
    val uri = Uri.parse("mailto:$email")

    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra(Intent.EXTRA_SUBJECT, parsedResult.subject ?: "")
        putExtra(Intent.EXTRA_TEXT, parsedResult.body ?: "")
    }

    return Intent.createChooser(intent, context.getString(R.string.intent_chooser_mail_title))
}

// ------------------------------------------ CALL PHONE -------------------------------------------

private fun createCallNumberIntent(phoneNumber: String): Intent {
    return Intent(Intent.ACTION_DIAL, Uri.parse(phoneNumber))
}

fun createCallPhoneNumberIntent(parsedResult: TelParsedResult): Intent {
    return createCallNumberIntent(parsedResult.telURI)
}

fun createCallSmsNumberIntent(parsedResult: SMSParsedResult): Intent {
    val phone = if(parsedResult.numbers?.isNotEmpty() == true) parsedResult.numbers.first() else ""
    return createCallNumberIntent("tel:$phone")
}

// ------------------------------------------ SEND SMS ---------------------------------------------

private fun createSendSmsIntent(uri: String): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(uri))
}

fun createSendSmsToPhoneNumberIntent(parsedResult: TelParsedResult): Intent {
    return createSendSmsIntent("smsto:${parsedResult.number}")
}

fun createSendSmsToSmsNumberIntent(parsedResult: SMSParsedResult): Intent {
    return createSendSmsIntent(parsedResult.smsuri)
}

// ------------------------------------------ SEARCH -----------------------------------------------

fun createSearchUrlIntent(url: String): Intent {
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
}