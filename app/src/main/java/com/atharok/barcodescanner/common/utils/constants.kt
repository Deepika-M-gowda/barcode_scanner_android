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

package com.atharok.barcodescanner.common.utils

// -------------------------------------- Scope Name Session ---------------------------------------

const val BARCODE_ANALYSIS_SCOPE_SESSION = "barcodeAnalysisSession"
const val BARCODE_ANALYSIS_SCOPE_SESSION_ID = "barcodeAnalysisSessionID"

const val ACTION_SCOPE_SESSION = "actionScopeSession"
const val ACTION_SCOPE_SESSION_ID = "actionScopeSessionID"

// ------------------------------------------ KOIN NAMED -------------------------------------------

const val DIALOG_SIMPLE_VIEW_KOIN_NAMED = "DialogSimpleViewKoinNamed"

// ------------------------------------------ INTENT -----------------------------------------------

const val INTENT_START_ACTIVITY = "intentStartActivity"
const val INTENT_PICK_IMAGE = "intentPickImage"
const val INTENT_PICK_CONTACT = "intentPickContact"
const val INTENT_PICK_WIFI_NETWORK = "intentPickWifiNetwork"
const val INTENT_WIFI_ADD_NETWORKS = "intentWifiAddNetworks"
const val INTENT_ACTION_CREATE_IMAGE = "intentActionCreateImage"
const val INTENT_SHARE_TEXT = "intentShareText"
const val INTENT_SHARE_IMAGE = "intentShareImage"
const val INTENT_ADD_AGENDA = "intentAddAgenda"
const val INTENT_ADD_CONTACT = "intentAddContact"
const val INTENT_ADD_EMAIL = "intentAddEmail"
const val INTENT_ADD_PHONE_NUMBER = "intentAddPhoneNumber"
const val INTENT_ADD_SMS_NUMBER = "intentAddSmsNumber"
const val INTENT_SEND_EMAIL = "intentSendEmail"
const val INTENT_CALL_PHONE_NUMBER = "intentCallPhoneNumber"
const val INTENT_CALL_SMS_NUMBER = "intentCallSmsNumber"
const val INTENT_SEND_SMS_TO_PHONE_NUMBER = "intentSendSmsToPhoneNumber"
const val INTENT_SEND_SMS_TO_SMS_NUMBER = "intentSendSmsToSmsNumber"
/*const val INTENT_SEARCH_LOCALISATION = "intentSearchLocalisation"
const val INTENT_SEARCH_URL = "intentSearchUrl"*/
const val INTENT_SEARCH_URL = "intentSearchUrl"

// ------------------------------------------ BUNDLE KEY -------------------------------------------

// Clé du Bundle associé à l'URI d'une image.
const val IMAGE_URI_KEY = "imageUriKey"

// Clé du Bundle associé au type (et sous type) BarcodeProduct
const val PRODUCT_KEY = "productKey"

// Clé du Bundle associé au type Barcode
const val BARCODE_KEY = "barcodeKey"

// Clé du Bundle associé au au type d'erreur lors de la recherche sur une API.
const val API_ERROR_KEY = "apiErrorKey"

// Clé de l'intent lors de la re-création de l'activity permettant de rechercher dans les APIs même lorsque désactivé dans les paramètres
const val IGNORE_USE_SEARCH_ON_API_SETTING_KEY = "ignoreUseSearchOnApiSettingKey"

// Clé du Bundle associé au message d'erreur lors de la recherche sur une API.
const val BARCODE_MESSAGE_ERROR_KEY = "barcodeMessageErrorKey"

// Clé du Bundle associé au type AllBarCodeCreatorType
const val BARCODE_TYPE_ENUM_KEY = "barcodeTypeEnumKey" // Clé de l'intent contenant le type de code-barres à générer (AllBarCodeCreatorType: QR_TEXT, QR_AGENDA, AZTEC, EAN_13, EAN_8, UPC_A, etc...)

// Clé du Bundle associé au type String, contenant le contents du Barcode
const val BARCODE_CONTENTS_KEY = "barcodeStringKey" // Clé de l'intent contenant le contenu du code-barres dans la transition entre le formulaire de création de code-barres et le résultat

// Clé du Bundle associé au type BarcodeFormat
const val BARCODE_FORMAT_KEY = "barcodeFormatKey"


// ---- Permet d'identifier le ViewPagerAdapter à instancier avec les bons paramètres dans Koin ----

/*const val FOOD_PRODUCT_VIEW_PAGER_ADAPTER = "foodProductViewPagerAdapter"
const val BOOK_PRODUCT_VIEW_PAGER_ADAPTER = "bookProductViewPagerAdapter"
const val DEFAULT_PRODUCT_VIEW_PAGER_ADAPTER = "defaultProductViewPagerAdapter"*/

// ------------------------------------------- API Links -------------------------------------------

// ---- URL des fichiers complémentaires pour OpenFoodFacts ----
const val LABELS_LOCALE_FILE_NAME = "labels.json"
const val LABELS_URL = "https://world.openfoodfacts.org/labels.json"

const val ADDITIVES_LOCALE_FILE_NAME = "additives.json"
const val ADDITIVES_URL = "https://world.openfoodfacts.org/data/taxonomies/additives.json"

const val ADDITIVES_CLASSES_LOCALE_FILE_NAME = "additives_classes.json"
const val ADDITIVES_CLASSES_URL = "https://world.openfoodfacts.org/data/taxonomies/additives_classes.json"

const val ALLERGENS_LOCALE_FILE_NAME = "allergens.json"
const val ALLERGENS_URL = "https://world.openfoodfacts.org/data/taxonomies/allergens.json"

const val COUNTRIES_LOCALE_FILE_NAME = "countries.json"
const val COUNTRIES_URL = "https://world.openfoodfacts.org/data/taxonomies/countries.json"

/*const val INGREDIENTS_ANALYSIS_LOCALE_FILE_NAME = "ingredients_analysis.json"
const val INGREDIENTS_ANALYSIS_URL = "https://world.openfoodfacts.org/data/taxonomies/ingredients_analysis.json"*/

// ------------------- URL des images du Nutriscore, NovaGroup et EcoScore -------------------------

const val NUTRISCORE_A_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-a.svg"
const val NUTRISCORE_B_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-b.svg"
const val NUTRISCORE_C_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-c.svg"
const val NUTRISCORE_D_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-d.svg"
const val NUTRISCORE_E_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-e.svg"
const val NUTRISCORE_UNKNOWN_URL = "https://static.openfoodfacts.org/images/misc/nutriscore-unknown.svg"

const val NOVA_GROUP_1_URL = "https://static.openfoodfacts.org/images/misc/nova-group-1.svg"
const val NOVA_GROUP_2_URL = "https://static.openfoodfacts.org/images/misc/nova-group-2.svg"
const val NOVA_GROUP_3_URL = "https://static.openfoodfacts.org/images/misc/nova-group-3.svg"
const val NOVA_GROUP_4_URL = "https://static.openfoodfacts.org/images/misc/nova-group-4.svg"
const val NOVA_GROUP_UNKNOWN_URL = "https://static.openfoodfacts.org/images/misc/nova-group-unknown.svg"

const val ECO_SCORE_A_URL = "https://static.openfoodfacts.org/images/icons/ecoscore-a.svg"
const val ECO_SCORE_B_URL = "https://static.openfoodfacts.org/images/icons/ecoscore-b.svg"
const val ECO_SCORE_C_URL = "https://static.openfoodfacts.org/images/icons/ecoscore-c.svg"
const val ECO_SCORE_D_URL = "https://static.openfoodfacts.org/images/icons/ecoscore-d.svg"
const val ECO_SCORE_E_URL = "https://static.openfoodfacts.org/images/icons/ecoscore-e.svg"
const val ECO_SCORE_UNKNOWN_URL = "https://static.openfoodfacts.org/images/attributes/ecoscore-unknown.svg"

// ----------------------------------------- Static Values -----------------------------------------

const val DATABASE_NAME = "scan_history.db"
const val ENCODING_UTF_8 = "UTF-8"
const val ENCODING_ISO_8859_1 = "ISO-8859-1"
const val BARCODE_IMAGE_SIZE = 2048

// ---- Valeures indicatives de la quantité des substances dans les produits alimentaires ----
const val FAT_VALUE_LOW = 3.0f
const val FAT_VALUE_HIGH = 20.0f

const val SATURATED_FAT_VALUE_LOW = 1.5f
const val SATURATED_FAT_VALUE_HIGH = 5.0f

const val SUGAR_VALUE_LOW = 5.0f
const val SUGAR_VALUE_HIGH = 12.5f

const val SALT_VALUE_LOW = 0.3f
const val SALT_VALUE_HIGH = 1.5f
