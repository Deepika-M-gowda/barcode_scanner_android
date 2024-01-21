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

package com.atharok.barcodescanner.domain.entity.barcode

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.atharok.barcodescanner.common.extensions.is1DIndustrialBarcode
import com.atharok.barcodescanner.common.extensions.is1DProductBarcode
import com.atharok.barcodescanner.common.extensions.is2DBarcode
import com.google.zxing.BarcodeFormat
import java.io.Serializable

@Keep
@Entity
data class Barcode(
    @ColumnInfo(name = "contents") var contents: String,
    @ColumnInfo(name = "format_name") val formatName: String,
    @PrimaryKey @ColumnInfo(name = "scan_date") val scanDate: Long,
    @ColumnInfo(name = "type") var type: String = BarcodeType.UNKNOWN.name,
    @ColumnInfo(name = "error_correction_level") var errorCorrectionLevel: String = QrCodeErrorCorrectionLevel.NONE.name,
    @ColumnInfo(name = "name") var name: String = ""
): Serializable {

    @Ignore
    var country: CountriesEnum? = determineBarcodeCountry(contents, getBarcodeFormat())
        private set

    @Ignore
    val is1DProductBarcodeFormat = getBarcodeFormat().is1DProductBarcode()

    @Ignore
    val is1DIndustrialBarcodeFormat = getBarcodeFormat().is1DIndustrialBarcode()

    @Ignore
    val is2DBarcodeFormat = getBarcodeFormat().is2DBarcode()

    @Ignore
    fun getBarcodeType(): BarcodeType = BarcodeType.valueOf(type)

    @Ignore
    fun getQrCodeErrorCorrectionLevel(): QrCodeErrorCorrectionLevel = QrCodeErrorCorrectionLevel.valueOf(errorCorrectionLevel)

    @Ignore
    fun getBarcodeFormat(): BarcodeFormat = BarcodeFormat.valueOf(formatName)

    @Ignore
    fun isBookBarcode(): Boolean = getBarcodeFormat() == BarcodeFormat.EAN_13 &&
            (contents.startsWith("978") || contents.startsWith("979"))

    @Ignore
    private fun determineBarcodeCountry(contents: String, barcodeFormat: BarcodeFormat): CountriesEnum? {
        return if(barcodeFormat == BarcodeFormat.EAN_13 || barcodeFormat == BarcodeFormat.UPC_A) {
            val value = convertToInt(contents)
            when (value) {
                in 0..19, in 60..99 -> CountriesEnum.USA //CountriesEnum.CANADA
                in 30..39, in 100..139 -> CountriesEnum.USA
                in 300..379 -> CountriesEnum.FRANCE //MONACO
                380 -> CountriesEnum.BULGARIA
                383 -> CountriesEnum.SLOVENIA
                385 -> CountriesEnum.CROATIA
                387 -> CountriesEnum.BOSNIA // && Herzegovina
                389 -> CountriesEnum.MONTENEGRO
                390 -> CountriesEnum.KOSOVO
                in 400..440 -> CountriesEnum.GERMANY
                in 450..459, in 490..499 -> CountriesEnum.JAPAN
                in 460..469 -> CountriesEnum.RUSSIA
                470 -> CountriesEnum.KYRGYZSTAN//KURDISTAN
                471 -> CountriesEnum.TAIWAN
                474 -> CountriesEnum.ESTONIA
                475 -> CountriesEnum.LATVIA
                476 -> CountriesEnum.AZERBAIJAN
                477 -> CountriesEnum.LITHUANIA
                478 -> CountriesEnum.UZBEKISTAN
                479 -> CountriesEnum.SRI_LANKA
                480 -> CountriesEnum.PHILIPPINES
                481 -> CountriesEnum.BELARUS
                482 -> CountriesEnum.UKRAINE
                483 -> CountriesEnum.TURKMENISTAN
                484 -> CountriesEnum.MOLDOVA
                485 -> CountriesEnum.ARMENIA
                486 -> CountriesEnum.GEORGIA
                487 -> CountriesEnum.KAZAKHSTAN
                488 -> CountriesEnum.TAJIKISTAN
                489 -> CountriesEnum.HONG_KONG
                in 500..509 -> CountriesEnum.UNITED_KINGDOM
                520, 521 -> CountriesEnum.GREECE
                528 -> CountriesEnum.LEBANON
                529 -> CountriesEnum.CYPRUS
                530 -> CountriesEnum.ALBANIA
                531 -> CountriesEnum.MACEDONIA //?
                535 -> CountriesEnum.MALTA
                539 -> CountriesEnum.IRELAND
                in 540..549 -> CountriesEnum.BELGIUM// && CountriesEnum.LUXEMBOURG
                560 -> CountriesEnum.PORTUGAL
                569 -> CountriesEnum.ISLAND
                in 570..579 -> CountriesEnum.DENMARK //  Faroe Islands and Greenland
                590 -> CountriesEnum.POLAND
                594 -> CountriesEnum.ROMANIA
                599 -> CountriesEnum.HUNGARY
                600, 601 -> CountriesEnum.SOUTH_AFRICA
                603 -> CountriesEnum.GHANA
                604 -> CountriesEnum.SENEGAL
                608 -> CountriesEnum.BAHRAIN
                609 -> CountriesEnum.MAURITIUS
                611 -> CountriesEnum.MOROCCO
                613 -> CountriesEnum.ALGERIA
                615 -> CountriesEnum.NIGERIA
                616 -> CountriesEnum.KENYA
                617 -> CountriesEnum.CAMEROON
                618 -> CountriesEnum.COTE_D_IVOIRE
                619 -> CountriesEnum.TUNISIA
                620 -> CountriesEnum.TANZANIA
                621 -> CountriesEnum.SYRIA
                622 -> CountriesEnum.EGYPT
                623 -> CountriesEnum.BRUNEI
                624 -> CountriesEnum.LIBYA
                625 -> CountriesEnum.JORDAN
                626 -> CountriesEnum.IRAN
                627 -> CountriesEnum.KUWAIT
                628 -> CountriesEnum.SAUDI_ARABIA
                629 -> CountriesEnum.UNITED_ARAB_EMIRATES
                630 -> CountriesEnum.QATAR
                in 640..649 -> CountriesEnum.FINLAND
                in 690..699 -> CountriesEnum.CHINA
                in 700..709 -> CountriesEnum.NORWAY
                729 -> CountriesEnum.ISRAEL
                in 730..739 -> CountriesEnum.SWEDEN
                740 -> CountriesEnum.GUATEMALA
                741 -> CountriesEnum.EL_SALVADOR
                742 -> CountriesEnum.HONDURAS
                743 -> CountriesEnum.NICARAGUA
                744 -> CountriesEnum.COSTA_RICA
                745 -> CountriesEnum.PANAMA
                746 -> CountriesEnum.DOMINICAN_REPUBLIC
                750 -> CountriesEnum.MEXICO
                754, 755 -> CountriesEnum.CANADA
                759 -> CountriesEnum.VENEZUELA
                in 760..769 -> CountriesEnum.SWITZERLAND // && Liechtenstein
                770, 771 -> CountriesEnum.COLOMBIA
                773 -> CountriesEnum.URUGUAY
                775 -> CountriesEnum.PERU
                777 -> CountriesEnum.BOLIVIA
                778, 779 -> CountriesEnum.ARGENTINA
                780 -> CountriesEnum.CHILE
                784 -> CountriesEnum.PARAGUAY
                786 -> CountriesEnum.ECUADOR
                789, 790 -> CountriesEnum.BRAZIL
                in 800..839 -> CountriesEnum.ITALY // & San Marino and Vatican City
                in 840..849 -> CountriesEnum.SPAIN // & Andorra
                850 -> CountriesEnum.CUBA
                858 -> CountriesEnum.SLOVAKIA
                859 -> CountriesEnum.CZECH_REPUBLIC
                860 -> CountriesEnum.SERBIA
                865 -> CountriesEnum.MONGOLIA
                867 -> CountriesEnum.NORTH_KOREA
                868, 869 -> CountriesEnum.TURKEY
                in 870..879 -> CountriesEnum.NETHERLANDS
                880 -> CountriesEnum.SOUTH_KOREA
                883 -> CountriesEnum.MYANMAR
                884 -> CountriesEnum.CAMBODIA
                885 -> CountriesEnum.THAILAND
                888 -> CountriesEnum.SINGAPORE
                890 -> CountriesEnum.INDIA
                893 -> CountriesEnum.VIETNAM
                896 -> CountriesEnum.PAKISTAN
                899 -> CountriesEnum.INDONESIA
                in 900..919 -> CountriesEnum.AUSTRIA
                in 930..939 -> CountriesEnum.AUSTRALIA
                in 940..949 -> CountriesEnum.NEW_ZEALAND
                //950 -> GLOBAL_OFFICE: Special Applications
                955 -> CountriesEnum.MALAYSIA
                958 -> CountriesEnum.MACAU
                /*977 -> CountriesEnum.ISSN
                978, 879 -> CountriesEnum.ISBN
                980 -> CountriesEnum.REFUND_RECEIPTS
                981, 984 -> CountriesEnum.COMMON_CURRENCY_COUPONS
                in 990..999 -> CountriesEnum.COUPONS*/
                else -> null
            }
        } else null
    }

    @Ignore
    fun updateCountry() {
        country = determineBarcodeCountry(contents, getBarcodeFormat())
    }

    @Ignore
    private fun convertToInt(text: String): Int {
        return if(text.length==12) text.substring(0, 2).toInt() else text.substring(0, 3).toInt()
    }
}